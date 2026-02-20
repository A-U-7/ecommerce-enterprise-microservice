package com.ecommerce.product.service.impl;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public ProductDto create(ProductRequest request) {
        Product entity = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
        Product saved = repository.save(entity);
        return toDto(saved);
    }

    @Override
    public ProductDto update(Long id, ProductRequest request) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategory(request.getCategory());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        Product updated = repository.save(existing);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        repository.delete(existing);
    }

    @Override
    public ProductDto findById(Long id) {
        Product p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return toDto(p);
    }

    @Override
    public Page<ProductDto> search(String name, String category, Pageable pageable) {
        Page<Product> page;
        if (name != null && !name.isEmpty()) {
            page = repository.findByNameContainingIgnoreCase(name, pageable);
        } else if (category != null && !category.isEmpty()) {
            page = repository.findByCategory(category, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    private ProductDto toDto(Product p) {
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .category(p.getCategory())
                .price(p.getPrice())
                .stock(p.getStock())
                .build();
    }
}
