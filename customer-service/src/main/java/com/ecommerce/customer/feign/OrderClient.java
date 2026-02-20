package com.ecommerce.customer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.order.dto.OrderDto;

import java.util.List;

@FeignClient(name = "order-service", url = "${order-service.url:http://localhost:8082}")
public interface OrderClient {
    @GetMapping("/api/v1/orders/customer/{customerId}")
    ApiResponse<List<OrderDto>> getOrdersByCustomer(@PathVariable("customerId") Long customerId);
}
