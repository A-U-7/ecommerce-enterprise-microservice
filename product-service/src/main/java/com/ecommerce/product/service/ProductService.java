package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDto create(ProductRequest request);
    ProductDto update(Long id, ProductRequest request);
    void delete(Long id);
    ProductDto findById(Long id);
    Page<ProductDto> search(String name, String category, Pageable pageable);
}
