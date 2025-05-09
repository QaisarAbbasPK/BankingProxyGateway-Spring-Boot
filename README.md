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

![Postman Token Generate](./images/token-pic.png)


The gateway:
- Validates the token.
- Checks if the vendor has an active route for `{service}`.
- Proxies the request to the configured backend.

---

## 🔌 Example Usage

curl -X POST \
  -H "Auth-Access-Token: Secure <your_vendor_jwt_token>" \
  -H "Content-Type: application/json" \
  http://localhost:8080/gateway/v1/jsonplaceholdler/posts

![Postman Posts](./images/service-pic.png)


```sql

-- MySQL

--
-- Database: `core_gateway`
--

-- --------------------------------------------------------

--
-- Table structure for table `service_routes`
--

CREATE TABLE `service_routes` (
  `id` bigint(20) NOT NULL,
  `connect_timeout` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `ip_address` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `read_timeout` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `service_key` varchar(255) NOT NULL,
  `service_name` varchar(255) NOT NULL,
  `service_url` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` varchar(255) NOT NULL,
  `vendor_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `service_routes`
--

INSERT INTO `service_routes` (`id`, `connect_timeout`, `created_at`, `ip_address`, `is_active`, `read_timeout`, `remarks`, `service_key`, `service_name`, `service_url`, `updated_at`, `updated_by`, `vendor_id`) VALUES
(1, 30000, '2025-05-09 13:26:32.000000', '192.11.10.12', b'1', 30000, 'JASON Place Holder', 'jsonplaceholder', 'jsonplaceholder', 'https://jsonplaceholder.typicode.com', '2025-05-09 13:26:32.000000', 'Ali', 1);

-- --------------------------------------------------------

--
-- Table structure for table `vendors`
--

CREATE TABLE `vendors` (
  `id` bigint(20) NOT NULL,
  `client_id` varchar(255) NOT NULL,
  `client_name` varchar(255) NOT NULL,
  `client_secret` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `ip_address` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `vendors`
--

INSERT INTO `vendors` (`id`, `client_id`, `client_name`, `client_secret`, `created_at`, `ip_address`, `is_active`, `remarks`, `updated_at`, `updated_by`) VALUES
(1, 'MXIMZ5QMRF1360DZYJOS1YXHYHWURD', 'Loan Service', '1Iter4t09TfUNBfBrS4AE70SkopRBaPRKtPpkh9QaDR4MxK2Lc', '2025-05-09 12:22:54.000000', '192.168.100.100', b'1', 'Loan Service Authorization Key', '2025-05-09 12:22:54.000000', 'Ali'),
(2, 'C7EPO7E3W17NRI0YTSAVMQMGH6B6BZ', 'RDS Service', 'RjXEUCNmcKnBqi9SVFkFCgtgDujhAXDj95rUIL8zwlkykwYZIF', '2025-05-09 12:22:54.000000', '192.168.100.100', b'1', 'RDS Service Authorization Key', '2025-05-09 12:22:54.000000', 'Ali');

-- --------------------------------------------------------

--
-- Table structure for table `vendor_tokens`
--

CREATE TABLE `vendor_tokens` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `ip_address` varchar(255) NOT NULL,
  `token` varchar(2000) NOT NULL,
  `vendor_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `vendor_tokens`
--

INSERT INTO `vendor_tokens` (`id`, `created_at`, `ip_address`, `token`, `vendor_id`) VALUES
(8, '2025-05-09 14:32:52.000000', 'IP Address', 'eyJhbGciOiJIUzI1NiJ9.eyJjbGllbnRfbmFtZSI6IkxvYW4gU2VydmljZSIsImNsaWVudF9pZCI6IlZGMlA0OEQ2UEg1RDIwRTU3OUxMMVhGSjRWRUZMWCIsInN1YiI6IlZGMlA0OEQ2UEg1RDIwRTU3OUxMMVhGSjRWRUZMWCIsImlhdCI6MTc0Njc4MzE3MiwiZXhwIjoxNzQ2ODY5NTcyfQ.bcUjaXye0KRUWaxYoHFf4ihPXVGnEJ1N9BgQetMlNpM', 1),

--
-- Indexes for dumped tables
--

--
-- Indexes for table `service_routes`
--
ALTER TABLE `service_routes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKiw46n8cndhpmc43cmttpcdhlv` (`service_key`),
  ADD UNIQUE KEY `UKog9ump0g88f52c3ms1po3xhuv` (`service_name`),
  ADD UNIQUE KEY `UKn2xbfyipqja5befw8ranmoh01` (`vendor_id`);

--
-- Indexes for table `vendors`
--
ALTER TABLE `vendors`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKkywnjt67fpnwovufsmn49e2b1` (`client_id`),
  ADD UNIQUE KEY `UKejanr9yv5kmvq5bakj206f2s1` (`client_secret`);

--
-- Indexes for table `vendor_tokens`
--
ALTER TABLE `vendor_tokens`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `service_routes`
--
ALTER TABLE `service_routes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `vendors`
--
ALTER TABLE `vendors`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `vendor_tokens`
--
ALTER TABLE `vendor_tokens`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;
COMMIT;

```

![Database](./images/db-pic.png)



### Dynamic service lookup

```java
ServiceRoute route = serviceRouteRepository
    .findByServiceKeyAndVendorIdAndIsActive(service, vendor.getId(), true)
    .orElseThrow(() -> new GlobalException("Service not found: " + service));
```

### Timeout-configured RestTemplate

```java
SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
requestFactory.setConnectTimeout(connectTimeout);
requestFactory.setReadTimeout(readTimeout);
```

### Path and query forwarding

```java
String targetUrl = backendBaseUrl + forwardPath + (queryString != null ? "?" + queryString : "");
```

## ⚙️ Setup

### Prerequisites

* Java 17+
* Maven 3.x
* A configured SQL database (H2, MySQL, PostgreSQL, etc.)

### Run Locally

```bash
mvn spring-boot:run
```

### Build Jar

```bash
mvn clean package
java -jar target/coreGateway.jar
```

---

## 🔑 Extending

✅ Add more vendors → insert into `vendors` table
✅ Add new services → insert into `service_routes` table
✅ Per-service timeouts → adjust `connectTimeout` and `readTimeout`
✅ Add extra security → extend Spring Security as needed

---

## 📝 Future Improvements

* Vendor-specific rate limiting
* Per-route logging and metrics
* Admin dashboard for managing vendors and routes
* WebFlux upgrade for non-blocking proxying
* Distributed caching for scalable RestTemplate instances

---

## 🙌 Contributing

Feel free to **fork**, **open issues**, or **submit PRs**!
This project aims to make dynamic service proxying easy, configurable, and secure in multi-vendor systems.

---

⭐ **Star this repo** if you find it useful!

```

---

✅ **Ready-to-paste GitHub README.md**  
✅ Includes headings, fenced code blocks, and clean section breaks  

If you want, I can also add GitHub badges (build, license, etc.) — just let me know! 🚀
```


Thank You!
Qaisar Abbas
https://linkedin.com/in/Qaisar-Abbas
