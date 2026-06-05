# User Service - Food Ordering System

Handles user registration, login (JWT), and profile management.

## Requirements
- Java 17+
- Maven
- PostgreSQL running locally

## Setup

1. Create the database:
```sql
CREATE DATABASE userservice_db;
```

2. Update credentials in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Run the service:
```bash
mvn spring-boot:run
```

Service starts on **http://localhost:8081**

## API Endpoints

| Method | URL | Auth | Description |
|--------|-----|------|-------------|
| POST | /auth/register | Public | Register a new user |
| POST | /auth/login | Public | Login, returns JWT |
| GET | /users/me | Bearer JWT | Get own profile |
| PUT | /users/me | Bearer JWT | Update email/password |

## Swagger UI
Open: http://localhost:8081/swagger-ui.html

Use the **Authorize** button to paste your Bearer token and test protected endpoints.

## Run Tests
```bash
mvn test
```

## Example Requests

### Register
```json
POST /auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123",
  "role": "CUSTOMER"
}
```

### Login
```json
POST /auth/login
{
  "username": "john",
  "password": "secret123"
}
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john",
  "role": "CUSTOMER"
}
```

### Get Profile
```
GET /users/me
Authorization: Bearer <token>
```
