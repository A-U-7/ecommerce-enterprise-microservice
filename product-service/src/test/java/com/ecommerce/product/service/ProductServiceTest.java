package com.ecommerce.product.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        ProductRequest req = ProductRequest.builder()
                .name("X")
                .category("C")
                .price(BigDecimal.ONE)
                .stock(1)
                .build();
        Product saved = Product.builder()
                .id(1L)
                .name("X")
                .category("C")
                .price(BigDecimal.ONE)
                .stock(1)
                .build();
        when(repository.save(any())).thenReturn(saved);
        ProductDto dto = service.create(req);
        assertEquals(1L, dto.getId());
        verify(repository).save(any());
    }

    @Test
    void findById_notFound_throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void search_returnsPage() {
        Product p = Product.builder().id(1L).name("A").category("C").price(BigDecimal.ONE).stock(1).build();
        Page<Product> page = new PageImpl<>(List.of(p));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        Page<ProductDto> result = service.search(null, null, PageRequest.of(0,10));
        assertEquals(1, result.getTotalElements());
    }
}
