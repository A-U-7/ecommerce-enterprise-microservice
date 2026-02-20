package com.ecommerce.customer.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.customer.dto.CustomerDto;
import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> register(@Valid @RequestBody CustomerRequest request) {
        CustomerDto dto = service.register(request);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> update(@PathVariable Long id,
                                                            @Valid @RequestBody CustomerRequest request) {
        CustomerDto dto = service.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getById(@PathVariable Long id) {
        CustomerDto dto = service.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(dto, null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDto> result = service.list(pageable);
        return ResponseEntity.ok(new ApiResponse<>(result, null));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> all() {
        return ResponseEntity.ok(new ApiResponse<>(service.getAll(), null));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ApiResponse<List<?>>> orderHistory(@PathVariable("id") Long customerId) {
        return ResponseEntity.ok(new ApiResponse<>(service.getOrderHistory(customerId), null));
    }
}
