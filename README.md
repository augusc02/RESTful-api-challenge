# RESTful API Challenge

RESTful service for managing **financial transactions**, built with **Spring Boot 3.4** following clean architecture and SOLID principles.

## 🏗️ Architecture

```
src/main/java/com/challenge/api/
├── RestfulApiChallengeApplication.java
├── config/
│   ├── OpenApiConfig.java          # Swagger/OpenAPI setup
│   └── DataInitializer.java        # Dev sample data seeder
├── domain/
│   └── transaction/                # Transaction bounded context
│       ├── controller/             # HTTP layer (TransactionController)
│       ├── service/                # Business logic (interface + impl)
│       ├── repository/             # Data access (Spring Data JPA)
│       ├── model/Transaction.java  # JPA entity
│       ├── dto/                    # TransactionRequest / TransactionResponse
│       ├── mapper/                 # MapStruct mapper
│       └── exception/              # Domain-specific exceptions & Handler
└── response/
    └── ApiResponse.java            # Generic envelope for API responses
```

## 🐳 Docker

### Build and run with Docker Compose (recommended)
```bash
docker compose up --build
```

### Or build and run manually
```bash
docker build -t restful-api-challenge .
docker run -p 8080:8080 restful-api-challenge
```

The Dockerfile uses a **multi-stage build**: a builder stage compiles the fat JAR, and a minimal JRE image serves as the runtime to keep the final image small and secure.

## 🚀 Running Locally (without Docker)

### Prerequisites
- Java 21+ (tested up to Java 25)
- No Maven installation needed — use the included wrapper

```bash
./mvnw spring-boot:run
```

### Run tests
```bash
./mvnw test
```

## 📋 API Endpoints

| Method | Endpoint                           | Description                              |
|--------|------------------------------------|------------------------------------------|
| PUT    | `/transactions/{id}`               | Create or update a transaction           |
| GET    | `/transactions`                    | List all transactions                    |
| GET    | `/transactions/{id}`               | Get transaction by id                    |
| GET    | `/transactions/types/{type}`       | Get transaction ids by type              |
| GET    | `/transactions/sum/{id}`           | Sum of transaction and all its children  |

## 📦 Response Format

Most `GET` endpoints return a consistent envelope:

```json
{
  "success": true,
  "data": { ... },
  "timestamp": "2024-01-01T00:00:00"
}
```

Error responses include a `message` field:
```json
{
  "success": false,
  "message": "Transaction not found with id: 99",
  "timestamp": "2024-01-01T00:00:00"
}
```

The `PUT /transactions/{id}` endpoint returns a standard status confirmation:

```json
{
  "status": "ok"
}
```

## 📚 API Documentation

Once running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console *(dev/default profile only)*

## ➕ Adding a New Domain

Replicate the `domain/transaction` package structure for each new resource:
1. `model/` → JPA entity
2. `dto/` → Request + Response DTOs
3. `mapper/` → MapStruct mapper interface
4. `repository/` → Spring Data JPA repository
5. `service/` → Interface + `ServiceImpl`
6. `controller/` → `@RestController`