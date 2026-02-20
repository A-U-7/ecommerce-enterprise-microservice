package com.ecommerce.product.kafka;

import com.ecommerce.common.dto.events.OrderCreatedEvent;
import com.ecommerce.common.dto.events.ProductStockUpdatedEvent;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final ProductRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "orders", groupId = "product-service")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);
        event.getItems().forEach(item -> {
            Optional<Product> opt = repository.findById(item.getProductId());
            if (opt.isPresent()) {
                Product p = opt.get();
                int newStock = p.getStock() - item.getQuantity();
                if (newStock < 0) {
                    log.warn("Insufficient stock for product {}. current={}, requested={}", p.getId(), p.getStock(), item.getQuantity());
                    newStock = 0;
                }
                p.setStock(newStock);
                repository.save(p);
                // publish stock update event
                ProductStockUpdatedEvent stockEvent = ProductStockUpdatedEvent.builder()
                        .productId(p.getId())
                        .newStock(p.getStock())
                        .build();
                kafkaTemplate.send("product-stock", stockEvent);
            } else {
                log.warn("Product {} not found while processing order {}", item.getProductId(), event.getOrderId());
            }
        });
    }
}
