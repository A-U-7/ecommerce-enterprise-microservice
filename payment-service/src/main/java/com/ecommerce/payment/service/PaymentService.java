package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentDto;
import com.ecommerce.payment.dto.PaymentRequest;

import java.util.List;

public interface PaymentService {
    PaymentDto process(PaymentRequest request);
    PaymentDto getById(Long id);
    List<PaymentDto> getByOrder(Long orderId);
}
