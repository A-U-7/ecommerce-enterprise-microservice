package com.ecommerce.customer.service.impl;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.ValidationException;
import com.ecommerce.customer.dto.AddressDto;
import com.ecommerce.customer.dto.CustomerDto;
import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.entity.Address;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.feign.OrderClient;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.customer.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final OrderClient orderClient;

    @Override
    public CustomerDto register(CustomerRequest request) {
        repository.findByEmail(request.getEmail()).ifPresent(c -> {
            throw new ValidationException("Email already registered");
        });
        Customer entity = toEntity(request);
        Customer saved = repository.save(entity);
        return toDto(saved);
    }

    @Override
    public CustomerDto update(Long id, CustomerRequest request) {
        Customer existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setEmail(request.getEmail());
        existing.setAddresses(request.getAddresses().stream().map(this::toEntity).collect(Collectors.toList()));
        Customer updated = repository.save(existing);
        return toDto(updated);
    }

    @Override
    public CustomerDto getById(Long id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Override
    public List<CustomerDto> getAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<CustomerDto> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    @Override
    public List<?> getOrderHistory(Long customerId) {
        ApiResponse<List<?>> resp = orderClient.getOrdersByCustomer(customerId);
        return resp.getData();
    }

    private Customer toEntity(CustomerRequest req) {
        List<Address> addresses = null;
        if (req.getAddresses() != null) {
            addresses = req.getAddresses().stream().map(this::toEntity).collect(Collectors.toList());
        }
        return Customer.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .addresses(addresses)
                .build();
    }

    private Address toEntity(AddressDto dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .zip(dto.getZip())
                .country(dto.getCountry())
                .build();
    }

    private CustomerDto toDto(Customer c) {
        return CustomerDto.builder()
                .id(c.getId())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .addresses(c.getAddresses() == null ? null : c.getAddresses().stream().map(this::toDto).collect(Collectors.toList()))
                .build();
    }

    private AddressDto toDto(Address a) {
        return AddressDto.builder()
                .id(a.getId())
                .street(a.getStreet())
                .city(a.getCity())
                .state(a.getState())
                .zip(a.getZip())
                .country(a.getCountry())
                .build();
    }
}
