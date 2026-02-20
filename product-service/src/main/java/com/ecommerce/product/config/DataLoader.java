package com.ecommerce.product.config;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final ProductRepository repo;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (repo.count() == 0) {
                repo.save(Product.builder()
                        .name("Widget A")
                        .description("Basic widget")
                        .category("Widgets")
                        .price(BigDecimal.valueOf(9.99))
                        .stock(100)
                        .build());
                repo.save(Product.builder()
                        .name("Gadget B")
                        .description("Advanced gadget")
                        .category("Gadgets")
                        .price(BigDecimal.valueOf(19.99))
                        .stock(50)
                        .build());
            }
        };
    }
}
