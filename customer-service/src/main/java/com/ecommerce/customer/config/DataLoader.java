package com.ecommerce.customer.config;

import com.ecommerce.customer.entity.Address;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.customer.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final CustomerRepository repo;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (repo.count() == 0) {
                Address a = Address.builder()
                        .street("100 Main St")
                        .city("Metropolis")
                        .state("NY")
                        .zip("12345")
                        .country("USA")
                        .build();
                Customer c = Customer.builder()
                        .firstName("Jane")
                        .lastName("Doe")
                        .email("jane@example.com")
                        .addresses(List.of(a))
                        .build();
                repo.save(c);
            }
        };
    }
}
