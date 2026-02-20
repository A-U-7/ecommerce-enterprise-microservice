package com.ecommerce.payment.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock
    private PaymentRepository repository;

    @InjectMocks
    private PaymentServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void process_createsPayment() {
        PaymentRequest req = new PaymentRequest(1L, BigDecimal.TEN);
        Payment saved = Payment.builder().id(1L).amount(BigDecimal.TEN).status(PaymentStatus.COMPLETED).build();
        when(repository.save(any(Payment.class))).thenReturn(saved);
        assertEquals(1L, service.process(req).getId());
    }

    @Test
    void getById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void getByOrder_returnsList() {
        when(repository.findByOrderId(5L)).thenReturn(List.of(new Payment()));
        assertEquals(1, service.getByOrder(5L).size());
    }
}
