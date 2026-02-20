# ecommerce-enterprise-microservice

Enterprise-grade Spring Boot microservices e-commerce platform.

This multi-module Maven project contains the following services:

* `common-lib` – shared DTOs and utilities
* `config-server` – Spring Cloud Config server (native, classpath)
* `service-registry` – Eureka server for service discovery
* `api-gateway` – Spring Cloud Gateway routing (Eureka load-balancing
* `order-service` – order lifecycle and validation
* `customer-service` – customer profiles
* `payment-service` – simulated payment processing


**Kafka event bus:** services communicate asynchronously using topics `orders`, `payments`, and `product-stock`. The `docker-compose` setup now includes Zookeeper and Kafka brokers.

## Build

```bash
mvn clean install
```

Each module can be run independently from its directory:

```bash
cd product-service && mvn spring-boot:run
```

Alternatively, start everything with Docker Compose (now includes Kafka):

```bash
docker-compose up --build
```
This will build images for all services and run them in the recommended order (registry, config, business services, gateway).
> **Recommended start-up order for full stack**
> 1. `service-registry` (Eureka server at port 8761)
> 2. `config-server` (Config server at port 8888)
> 3. Business services (`product-service`, `order-service`, `customer-service`, `payment-service`)
> 4. `api-gateway` (gateway port configured via config server or application.yml)

After starting, use the gateway endpoint (e.g. `http://localhost:8080/api/v1/products`) to reach underlying services.
