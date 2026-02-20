package com.ecommerce.payment.kafka;

import com.ecommerce.common.dto.events.OrderCreatedEvent;
import com.ecommerce.common.dto.events.PaymentProcessedEvent;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final PaymentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "orders", groupId = "payment-service")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);
        // simulate payment processing once order is created
        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .amount(event.getTotal())
                .status(PaymentStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Payment saved = repository.save(payment);

        // publish payment processed event
        PaymentProcessedEvent processed = PaymentProcessedEvent.builder()
                .orderId(saved.getOrderId())
                .paymentId(saved.getId())
                .status(saved.getStatus())
                .build();
        kafkaTemplate.send("payments", processed);
    }
}
