package com.ecommerce.product.kafka;

import com.ecommerce.common.dto.events.OrderCreatedEvent;
import com.ecommerce.common.dto.events.OrderCreatedEvent.OrderItem;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
@EmbeddedKafka(partitions = 1, topics = {"orders", "product-stock"})
@DirtiesContext
class OrderEventListenerIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        // seed product
        Product p = Product.builder()
                .name("widget")
                .category("general")
                .price(BigDecimal.TEN)
                .stock(10)
                .build();
        repository.save(p);
    }

    @Test
    void whenOrderCreated_stockIsDecremented() {
        Product existing = repository.findAll().get(0);
        OrderItem item = OrderItem.builder()
                .productId(existing.getId())
                .quantity(3)
                .price(existing.getPrice())
                .build();
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(1L)
                .customerId(1L)
                .total(existing.getPrice().multiply(BigDecimal.valueOf(3)))
                .items(Collections.singletonList(item))
                .build();

        kafkaTemplate.send("orders", event);

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Product updated = repository.findById(existing.getId()).get();
            assertEquals(7, updated.getStock());
        });
    }
}
