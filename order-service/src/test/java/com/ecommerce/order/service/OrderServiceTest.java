package com.ecommerce.order.service;

import com.ecommerce.common.dto.ProductInfo;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.ValidationException;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.feign.ProductClient;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository repository;
    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrder_withEmptyItems_throws() {
        OrderRequest req = OrderRequest.builder().customerId(1L).items(List.of()).build();
        assertThrows(ValidationException.class, () -> service.placeOrder(req));
    }

    @Test
    void placeOrder_success() {
        OrderItemDto item = OrderItemDto.builder().productId(1L).quantity(2).price(BigDecimal.ONE).build();
        OrderRequest req = OrderRequest.builder().customerId(1L).items(List.of(item)).build();
        ProductInfo info = ProductInfo.builder().id(1L).price(BigDecimal.ONE).stock(10).build();
        when(productClient.getProduct(1L)).thenReturn(new com.ecommerce.common.dto.ApiResponse<>(info, null));
        Order saved = Order.builder().id(1L).customerId(1L).status(OrderStatus.CREATED)
                .total(BigDecimal.valueOf(2)).build();
        when(repository.save(any(Order.class))).thenReturn(saved);

        assertEquals(1L, service.placeOrder(req).getId());
    }

    @Test
    void updateStatus_invalidStatus_throws() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Order()));
        assertThrows(ValidationException.class, () -> service.updateStatus(1L, "INVALID"));
    }

    @Test
    void getById_notFound_throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void list_returnsPage() {
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        assertEquals(1, service.list(PageRequest.of(0, 10)).getTotalElements());
    }
}
