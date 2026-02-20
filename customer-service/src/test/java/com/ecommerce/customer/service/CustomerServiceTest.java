package com.ecommerce.customer.service;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.ValidationException;
import com.ecommerce.customer.dto.AddressDto;
import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.dto.CustomerDto;
import com.ecommerce.customer.entity.Address;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.feign.OrderClient;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    @Mock
    private CustomerRepository repository;
    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private CustomerServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_duplicateEmail_throws() {
        CustomerRequest req = CustomerRequest.builder().email("a@b.com").firstName("x").lastName("y").build();
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(new Customer()));
        assertThrows(ValidationException.class, () -> service.register(req));
    }

    @Test
    void register_success() {
        CustomerRequest req = CustomerRequest.builder().email("a@b.com").firstName("x").lastName("y").build();
        Customer saved = Customer.builder().id(1L).email("a@b.com").build();
        when(repository.save(any(Customer.class))).thenReturn(saved);
        CustomerRequest req2 = CustomerRequest.builder().email("a@b.com").firstName("x").lastName("y").build();
        CustomerDto dto = service.register(req2);
        assertEquals(1L, dto.getId());
    }

    @Test
    void getById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void list_returnsPage() {
        Page<Customer> page = new PageImpl<>(List.of(new Customer()));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        assertEquals(1, service.list(PageRequest.of(0,10)).getTotalElements());
    }
}
