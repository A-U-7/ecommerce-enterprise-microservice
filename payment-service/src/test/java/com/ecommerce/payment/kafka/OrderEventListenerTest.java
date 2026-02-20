package com.ecommerce.payment.kafka;

import com.ecommerce.common.dto.events.OrderCreatedEvent;
import com.ecommerce.common.dto.events.PaymentProcessedEvent;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderEventListenerTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderEventListener listener;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    @Captor
    private ArgumentCaptor<PaymentProcessedEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleOrderCreated_createsPaymentAndPublishesEvent() {
        OrderCreatedEvent.OrderItem item = OrderCreatedEvent.OrderItem.builder()
                .productId(1L)
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(5L)
                .customerId(2L)
                .total(BigDecimal.TEN)
                .items(Collections.singletonList(item))
                .build();

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        listener.handleOrderCreated(event);

        verify(repository).save(paymentCaptor.capture());
        Payment saved = paymentCaptor.getValue();
        assertEquals(5L, saved.getOrderId());
        assertEquals(PaymentStatus.COMPLETED, saved.getStatus());

        verify(kafkaTemplate).send(eq("payments"), eventCaptor.capture());
        PaymentProcessedEvent sent = eventCaptor.getValue();
        assertEquals(5L, sent.getOrderId());
        assertEquals(saved.getStatus(), sent.getStatus());
    }
}
