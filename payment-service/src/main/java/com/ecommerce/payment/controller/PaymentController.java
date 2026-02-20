package com.ecommerce.payment.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.payment.dto.PaymentDto;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> process(@Valid @RequestBody PaymentRequest request) {
        PaymentDto dto = service.process(request);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDto>> getById(@PathVariable Long id) {
        PaymentDto dto = service.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> byOrder(@PathVariable Long orderId) {
        List<PaymentDto> list = service.getByOrder(orderId);
        return ResponseEntity.ok(new ApiResponse<>(list, null));
    }
}
