package com.ecommerce.payment.config;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final PaymentRepository repo;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (repo.count() == 0) {
                repo.save(Payment.builder()
                        .orderId(1L)
                        .amount(BigDecimal.valueOf(9.99))
                        .status(PaymentStatus.COMPLETED)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }
        };
    }
}
