# 🌐 Core Custom Gateway

A **Spring Boot 3.4**-based dynamic **proxy gateway** built with **Java 17**.  
This gateway routes incoming HTTP requests to backend services dynamically, using **database-configured service routes** and **vendor-based JWT authentication**.

---

## ✨ Features

✅ Dynamically add vendor services via the database  
✅ Protect each service with client ID and client secret (JWT-based)  
✅ Proxy requests dynamically with configurable timeouts  
✅ Graceful error handling and transparent backend response passthrough  
✅ Reuse RestTemplate instances efficiently using timeout-based caching

---

## 🏗️ Tech Stack

- Spring Boot **3.4**
- Java **17**
- Spring Security (JWT-based authentication)
- Spring Data JPA
- Database: H2 / MySQL / PostgreSQL (your choice)

---

## 🗄️ Database Design

### Vendors Table (`vendors`)

| Field          | Type    | Description                               |
|---------------|---------|-----------------------------------------|
| id            | Long    | Primary key                             |
| client_id     | String  | Unique client identifier                |
| client_secret | String  | Secret key for JWT                      |
| is_active     | Boolean | Whether this vendor is active           |

### Service Routes Table (`service_routes`)

| Field            | Type    | Description                                       |
|-----------------|---------|--------------------------------------------------|
| id              | Long    | Primary key                                       |
| vendor_id       | Long    | Foreign key → Vendors table                       |
| service_key     | String  | Path segment used in `{service}` route            |
| service_url     | String  | Target backend base URL                           |
| connect_timeout | Integer | Connection timeout in ms (default 30000)          |
| read_timeout    | Integer | Read timeout in ms (default 30000)                |
| is_active       | Boolean | Whether this service route is active              |

---

## 🔒 Authentication Flow

1️⃣ **Vendor registers** → gets `clientId` and `clientSecret`.  
2️⃣ **Vendor requests a JWT token** using these credentials.  
3️⃣ **Vendor calls gateway endpoint**:

POST /gateway/v1/{service}/**
Auth-Access-Token: Secure <JWT_TOKEN>




The gateway:
- Validates the token.
- Checks if the vendor has an active route for `{service}`.
- Proxies the request to the configured backend.

---

## 🔌 Example Usage

### 1️⃣ Add a Vendor


```sql
INSERT INTO vendors (client_id, client_secret, is_active)
VALUES ('vendor1', 'supersecret', true);


INSERT INTO service_routes (vendor_id, service_key, service_url, connect_timeout, read_timeout, is_active)
VALUES (1, 'orders', 'https://api.vendor1.com/orders', 10000, 15000, true);


curl -X POST \
  -H "Auth-Access-Token: Secure <your_vendor_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"orderId":12345}' \
  http://localhost:8080/gateway/v1/orders/process

| Error Type               | Gateway Response                           |
| ------------------------ | ------------------------------------------ |
| 4xx or 5xx backend error | Pass through backend status, headers, body |
| Timeout or access error  | Return 400 with structured ApiResponse     |
| Unexpected exceptions    | Return 400 with detailed error message     |


Thank You!
Qaisar Abbas
https://linkedin.com/in/Qaisar-Abbas
