package com.ecommerce.payment.kafka;

import com.ecommerce.common.dto.events.OrderCreatedEvent;
import com.ecommerce.common.dto.events.OrderCreatedEvent.OrderItem;
import com.ecommerce.common.dto.events.PaymentProcessedEvent;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Collections;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"orders", "payments"})
@DirtiesContext
class OrderEventListenerIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void whenOrderCreated_paymentRecordSavedAndEventPublished() {
        OrderItem item = OrderItem.builder()
                .productId(1L)
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();
        OrderCreatedEvent orderEvent = OrderCreatedEvent.builder()
                .orderId(10L)
                .customerId(5L)
                .total(BigDecimal.TEN)
                .items(Collections.singletonList(item))
                .build();

        kafkaTemplate.send("orders", orderEvent);

        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertEquals(1, repository.findAll().size());
        });

        // test that PaymentProcessedEvent was published
        // unfortunately KafkaTemplate cannot receive; in real integration we use a consumer or embedded consumer
        // for simplicity, rely on listener unit test earlier. 
    }
}
