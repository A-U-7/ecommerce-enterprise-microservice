# Order Service

Handles order lifecycle and integrates with Product Service for stock/price validation.

**Base URL:** `http://localhost:8082/api/v1/orders`

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST   | `/`  | Place a new order (JSON body) |
| PUT    | `/{id}/status` | Update order status via `?status=` |
| GET    | `/{id}` | Get order by ID |
| GET    | `/` | List orders (paginated) |
| GET    | `/customer/{customerId}` | Orders for a customer |

## Sample Request

```json
POST /api/v1/orders
{
  "customerId": 42,
  "items": [
    {"productId": 1, "quantity": 2, "price": 9.99}
  ]
}
```

## Database schema (H2)

```sql
CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT,
  status VARCHAR(50),
  total DECIMAL(19,2),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
CREATE TABLE order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT,
  product_id BIGINT,
  quantity INTEGER,
  price DECIMAL(19,2)
);
```

## Configuration

In `application.yml` uses in‑memory H2. Feign timeouts set.

## Running

```
cd order-service
mvn spring-boot:run
```

## Notes

- Feign client points to `http://localhost:8081` for product service; adapt via properties or Eureka later.
- Basic status enum: CREATED, PAID, SHIPPED, CANCELLED.

### Kafka Events

- Publishes `OrderCreatedEvent` to topic `orders` when a new order is placed.
- Listens to `PaymentProcessedEvent` on topic `payments` to update order status to PAID.
