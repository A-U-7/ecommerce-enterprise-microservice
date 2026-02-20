package com.ecommerce.payment.service.impl;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.payment.dto.PaymentDto;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    @Override
    public PaymentDto process(PaymentRequest request) {
        // simulate payment processing, always succeed for now
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status(PaymentStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Payment saved = repository.save(payment);
        // in a real system, publish event or call order-service to confirm
        return toDto(saved);
    }

    @Override
    public PaymentDto getById(Long id) {
        Payment p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return toDto(p);
    }

    @Override
    public List<PaymentDto> getByOrder(Long orderId) {
        return repository.findByOrderId(orderId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private PaymentDto toDto(Payment p) {
        return PaymentDto.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .amount(p.getAmount())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
