package com.ecommerce.order.kafka;

import com.ecommerce.common.dto.events.PaymentProcessedEvent;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final OrderRepository repository;

    @KafkaListener(topics = "payments", groupId = "order-service")
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent: {}", event);
        Order order = repository.findById(event.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", event.getOrderId()));
        order.setStatus(OrderStatus.PAID);
        repository.save(order);
    }
}
