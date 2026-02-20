package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.CustomerDto;
import com.ecommerce.customer.dto.CustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerDto register(CustomerRequest request);
    CustomerDto update(Long id, CustomerRequest request);
    CustomerDto getById(Long id);
    List<CustomerDto> getAll();
    Page<CustomerDto> list(Pageable pageable);
    List<?> getOrderHistory(Long customerId);
}
