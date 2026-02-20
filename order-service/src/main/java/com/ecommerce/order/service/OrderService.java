package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(OrderRequest request);
    OrderDto updateStatus(Long orderId, String status);
    OrderDto getById(Long id);
    List<OrderDto> getByCustomer(Long customerId);
    Page<OrderDto> list(Pageable pageable);
}
