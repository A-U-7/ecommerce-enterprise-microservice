# Payment Service

Simulates payment processing for orders.

**Base URL:** `http://localhost:8084/api/v1/payments`

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST   | `/`  | Process payment (JSON body) |
| GET    | `/{id}` | Get payment by ID |
| GET    | `/order/{orderId}` | Payments for an order |

## Sample Request

```json
POST /api/v1/payments
{
  "orderId": 100,
  "amount": 29.99
}
```

## Database schema (H2)

```sql
CREATE TABLE payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT,
  amount DECIMAL(19,2),
  status VARCHAR(50),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

## Configuration

### Kafka Events

- Consumes `OrderCreatedEvent` from topic `orders` and publishes `PaymentProcessedEvent` to topic `payments`.

In-memory H2 database configured; no external dependencies.

## Running

```
cd payment-service
mvn spring-boot:run
```

## Notes

- Payment processing is mocked; every request returns `COMPLETED`.
- You could extend this to call Order Service via Feign to notify status.
