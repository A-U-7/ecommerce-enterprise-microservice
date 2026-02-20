# Config Server

Serves centralized configuration for all microservices. Uses the native profile and reads files from `classpath:/config`.

**Port:** 8888

### Available config

- `application.yml` (common defaults)
- `product-service.yml`
- `order-service.yml`
- `customer-service.yml`
- `payment-service.yml`

Run with:

```bash
cd config-server
mvn spring-boot:run
```

Services must import configuration via `spring.config.import=optional:configserver:http://localhost:8888`.
