package com.ecommerce.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductClient {
    @GetMapping("/api/v1/products/{id}")
    com.ecommerce.common.dto.ApiResponse<com.ecommerce.common.dto.ProductInfo> getProduct(@PathVariable("id") Long id);
}
