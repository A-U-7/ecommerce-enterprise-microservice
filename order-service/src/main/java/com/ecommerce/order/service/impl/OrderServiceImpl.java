package com.ecommerce.order.service.impl;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.ValidationException;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.feign.ProductClient;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public OrderDto placeOrder(OrderRequest request) {
        // basic validation
        if (request.getItems().isEmpty()) {
            throw new ValidationException("Order must contain at least one item");
        }

        // validate products exist and calculate total
        List<OrderItem> items = request.getItems().stream().map(this::toEntity).collect(Collectors.toList());
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .status(OrderStatus.CREATED)
                .total(total)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(items)
                .build();

        Order saved = repository.save(order);
        // publish event asynchronously
        kafkaTemplate.send("orders", toOrderCreatedEvent(saved));
        return toDto(saved);
    }

    @Override
    public OrderDto updateStatus(Long orderId, String status) {
        Order existing = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status: " + status);
        }
        existing.setStatus(newStatus);
        existing.setUpdatedAt(LocalDateTime.now());
        Order updated = repository.save(existing);
        return toDto(updated);
    }

    @Override
    public OrderDto getById(Long id) {
        Order o = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return toDto(o);
    }

    @Override
    public List<OrderDto> getByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<OrderDto> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    private Order toEntity(OrderItemDto dto) {
        // call product service to verify existence and get price
        ApiResponse<com.ecommerce.common.dto.ProductInfo> resp = productClient.getProduct(dto.getProductId());
        if (resp.getData() == null) {
            throw new ValidationException("Product " + dto.getProductId() + " not found");
        }
        com.ecommerce.common.dto.ProductInfo info = resp.getData();
        BigDecimal price = info.getPrice();
        return OrderItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .price(price)
                .build();
    }

    private OrderDto toDto(Order o) {
        return OrderDto.builder()
                .id(o.getId())
                .customerId(o.getCustomerId())
                .status(o.getStatus())
                .total(o.getTotal())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .items(o.getItems().stream()
                        .map(i -> OrderItemDto.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderCreatedEvent toOrderCreatedEvent(Order o) {
        return OrderCreatedEvent.builder()
                .orderId(o.getId())
                .customerId(o.getCustomerId())
                .total(o.getTotal())
                .items(o.getItems().stream()
                        .map(i -> OrderCreatedEvent.OrderItem.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
