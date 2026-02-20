package com.ecommerce.order.config;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final OrderRepository repo;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (repo.count() == 0) {
                OrderItem item = OrderItem.builder()
                        .productId(1L)
                        .quantity(1)
                        .price(BigDecimal.valueOf(9.99))
                        .build();
                Order order = Order.builder()
                        .customerId(1L)
                        .status(OrderStatus.CREATED)
                        .total(BigDecimal.valueOf(9.99))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .items(List.of(item))
                        .build();
                repo.save(order);
            }
        };
    }
}
