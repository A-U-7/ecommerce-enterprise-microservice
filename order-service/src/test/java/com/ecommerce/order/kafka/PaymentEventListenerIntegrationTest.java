package com.ecommerce.order.kafka;

import com.ecommerce.common.dto.events.PaymentProcessedEvent;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"payments"})
@DirtiesContext
class PaymentEventListenerIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private OrderRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        Order order = Order.builder()
                .customerId(1L)
                .status(OrderStatus.CREATED)
                .build();
        repository.save(order);
    }

    @Test
    void whenPaymentProcessed_orderStatusUpdates() {
        Order existing = repository.findAll().get(0);
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .orderId(existing.getId())
                .paymentId(100L)
                .status(OrderStatus.PAID)
                .build();
        kafkaTemplate.send("payments", event);

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Order updated = repository.findById(existing.getId()).get();
            assertEquals(OrderStatus.PAID, updated.getStatus());
        });
    }
}
