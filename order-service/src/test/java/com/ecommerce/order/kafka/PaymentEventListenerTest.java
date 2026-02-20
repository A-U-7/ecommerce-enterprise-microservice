package com.ecommerce.order.kafka;

import com.ecommerce.common.dto.events.PaymentProcessedEvent;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentEventListenerTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private PaymentEventListener listener;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handlePaymentProcessed_updatesOrderStatus() {
        Order existing = Order.builder()
                .id(10L)
                .status(OrderStatus.CREATED)
                .build();
        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .orderId(10L)
                .paymentId(99L)
                .status(OrderStatus.PAID)
                .build();

        listener.handlePaymentProcessed(event);

        verify(repository).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals(OrderStatus.PAID, saved.getStatus());
    }
}
