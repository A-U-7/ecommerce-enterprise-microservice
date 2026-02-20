package com.ecommerce.order.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> place(@Valid @RequestBody OrderRequest request) {
        OrderDto dto = service.placeOrder(request);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateStatus(@PathVariable Long id,
                                                              @RequestParam String status) {
        OrderDto dto = service.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getById(@PathVariable Long id) {
        OrderDto dto = service.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> result = service.list(pageable);
        return ResponseEntity.ok(new ApiResponse<>(result, null));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> byCustomer(@PathVariable Long customerId) {
        List<OrderDto> result = service.getByCustomer(customerId);
        return ResponseEntity.ok(new ApiResponse<>(result, null));
    }
}
