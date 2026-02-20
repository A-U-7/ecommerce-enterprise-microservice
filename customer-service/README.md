# Customer Service

Manages customers, profiles, addresses and fetches order history from Order Service.

**Base URL:** `http://localhost:8083/api/v1/customers`

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST   | `/`  | Register new customer |
| PUT    | `/{id}` | Update customer |
| GET    | `/{id}` | Get customer by ID |
| GET    | `/` | Paginated list of customers |
| GET    | `/all` | All customers (unpaged) |
| GET    | `/{id}/orders` | Order history for customer |

## Sample Request

```json
POST /api/v1/customers
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "addresses": [
    {"street":"123 Main","city":"City","state":"ST","zip":"12345","country":"USA"}
  ]
}
```

## Database schema (H2)

```sql
CREATE TABLE customers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  email VARCHAR(255)
);
CREATE TABLE addresses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT,
  street VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  zip VARCHAR(255),
  country VARCHAR(255)
);
```

## Configuration

In-memory H2 used; Feign client configured to call order-service via property.

## Running

```
cd customer-service
mvn spring-boot:run
```

## Notes

- Order history endpoint relies on Order Service; make sure it’s running.
- Email uniqueness enforced on registration.
