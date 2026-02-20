package com.ecommerce.product.kafka;

import com.ecommerce.common.dto.events.OrderCreatedEvent;
import com.ecommerce.common.dto.events.ProductStockUpdatedEvent;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderEventListenerTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderEventListener listener;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Captor
    private ArgumentCaptor<ProductStockUpdatedEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleOrderCreated_reducesStockAndPublishesEvent() {
        // prepare
        Product existing = Product.builder()
                .id(1L)
                .name("test")
                .price(BigDecimal.valueOf(10))
                .stock(5)
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        OrderCreatedEvent.OrderItem item = OrderCreatedEvent.OrderItem.builder()
                .productId(1L)
                .quantity(2)
                .price(BigDecimal.valueOf(10))
                .build();
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(100L)
                .customerId(50L)
                .total(BigDecimal.valueOf(20))
                .items(Collections.singletonList(item))
                .build();

        // act
        listener.handleOrderCreated(event);

        // verify stock updated and saved
        verify(repository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();
        assertEquals(3, saved.getStock());

        // verify event published
        verify(kafkaTemplate).send(eq("product-stock"), eventCaptor.capture());
        ProductStockUpdatedEvent sent = eventCaptor.getValue();
        assertEquals(1L, sent.getProductId());
        assertEquals(3, sent.getNewStock());
    }
}
