# Airline Booking System - Legacy Profile Module

## Overview

This is a **legacy-style** Spring Boot application that replicates the profile management functionality from the original console-based airline booking system. It intentionally maintains anti-patterns and legacy coding practices to demonstrate the need for modernization in the assignment.

## Business Domain

- **Profile Management**: Customer, Staff, and Passenger profiles
- **Authentication**: Plain-text password validation (legacy anti-pattern)
- **Validation**: Regex-based validation scattered in entity classes
- **Persistence**: Supabase PostgreSQL database

## Technology Stack

- **Framework**: Spring Boot 4.0.0
- **Database**: Supabase (PostgreSQL)
- **Language**: Java 17
- **Dependencies**: Spring Data JPA, Lombok, PostgreSQL Driver

## Architecture (Legacy Anti-Patterns Intentional)

### Entity Classes

Located in `src/main/java/com/example/springboot/entity/`

1. **Person.java** (Abstract Base Class)

   - Common attributes: name, email, phone, gender
   - Contains validation logic (anti-pattern: business logic in entity)
   - Regex patterns: email, phone, name, gender

2. **Customer.java** extends Person

   - IC Number: Format `XXXXXX-XX-XXXX` (e.g., "040225-14-1143")
   - Password: Plain text, exactly 8 characters (anti-pattern)
   - Validation methods mixed in entity

3. **Staff.java** extends Person

   - Staff ID: Format "S001", "S002", etc.
   - Position: "Manager", "Airline Controller"
   - Password: 5-digit numeric (anti-pattern: weak security)

4. **Passenger.java** extends Person
   - Passport Number: Format `[A-Z]\d{8}` (e.g., "A12345678")
   - Minimal class for ticket assignment

### Repository Layer

Located in `src/main/java/com/example/springboot/repository/`

- **CustomerRepository**: CRUD + custom queries (IC, email, phone lookup)
- **StaffRepository**: CRUD + staff ID/password authentication
- **PassengerRepository**: CRUD + passport number lookup

### Controller Layer

Located in `src/main/java/com/example/springboot/controller/`

Controllers implement legacy-style REST APIs with:

- Console-like validation scattered throughout
- No DTOs (anti-pattern: entities exposed directly)
- No service layer (anti-pattern: business logic in controllers)
- Plain text password handling (anti-pattern: security issue)

## API Endpoints

### Customer Endpoints

Base URL: `/api/customers`

| Method | Endpoint         | Description                             |
| ------ | ---------------- | --------------------------------------- |
| POST   | `/login`         | Customer authentication (IC + password) |
| POST   | `/register`      | Register new customer with validation   |
| GET    | `/`              | Get all customers                       |
| GET    | `/{id}`          | Get customer by ID                      |
| GET    | `/ic/{icNumber}` | Get customer by IC number               |
| PUT    | `/{id}`          | Update customer profile                 |
| DELETE | `/{id}`          | Delete customer                         |

**Login Request Example:**

```json
{
  "icNumber": "040225-14-1143",
  "password": "12345678"
}
```

**Register Request Example:**

```json
{
  "custIcNo": "123456-12-1234",
  "custPassword": "abcd1234",
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "012-3456789",
  "gender": "Male"
}
```

### Staff Endpoints

Base URL: `/api/staff`

| Method | Endpoint             | Description                                       |
| ------ | -------------------- | ------------------------------------------------- |
| POST   | `/login`             | Staff authentication (staffId + numeric password) |
| POST   | `/`                  | Create new staff member                           |
| GET    | `/`                  | Get all staff                                     |
| GET    | `/{id}`              | Get staff by ID                                   |
| GET    | `/staffid/{staffId}` | Get staff by staff ID                             |
| PUT    | `/{id}`              | Update staff profile                              |
| DELETE | `/{id}`              | Delete staff                                      |

**Staff Login Request Example:**

```json
{
  "staffId": "S001",
  "password": 11111
}
```

### Passenger Endpoints

Base URL: `/api/passengers`

| Method | Endpoint                 | Description               |
| ------ | ------------------------ | ------------------------- |
| POST   | `/`                      | Create new passenger      |
| GET    | `/`                      | Get all passengers        |
| GET    | `/{id}`                  | Get passenger by ID       |
| GET    | `/passport/{passportNo}` | Get passenger by passport |
| PUT    | `/{id}`                  | Update passenger          |
| DELETE | `/{id}`                  | Delete passenger          |

## Setup Instructions

### Prerequisites

1. Java 17 or higher
2. Maven 3.6+
3. Supabase account (free tier works)
4. IDE with Spring Boot support (IntelliJ IDEA, VS Code, Eclipse)

### Supabase Configuration

#### 1. Create Supabase Project

1. Go to [https://supabase.com](https://supabase.com)
2. Sign up / Log in
3. Create a new project
4. Note your database credentials

#### 2. Get Database Connection Details

1. In Supabase Dashboard → Settings → Database
2. Copy:
   - **Host**: `db.<project-id>.supabase.co`
   - **Database name**: `postgres`
   - **Port**: `5432`
   - **User**: `postgres`
   - **Password**: Your database password

#### 3. Update `application.properties`

Edit `src/main/resources/application.properties`:

```properties
# Replace with your Supabase credentials
spring.datasource.url=jdbc:postgresql://db.your-project-id.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your-password-here
```

**Example:**

```properties
spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=MySecurePassword123!
```

### Running the Application

#### Option 1: Using Maven

```bash
cd springboot
mvn clean install
mvn spring-boot:run
```

#### Option 2: Using VS Code

1. Open the `springboot` folder in VS Code
2. Open `SpringbootApplication.java`
3. Click "Run" above the `main` method
4. Or press `F5` to debug

#### Option 3: Using IntelliJ IDEA

1. Open `springboot` folder as a Maven project
2. Right-click `SpringbootApplication.java`
3. Select "Run 'SpringbootApplication'"

### Verify Setup

The application will:

1. Connect to Supabase
2. Create database tables automatically (JPA DDL auto)
3. Seed legacy sample data
4. Start server on port 8081

**Expected Console Output:**

```
========================================
🚀 Airline Booking Legacy System Starting...
========================================
📝 Seeding Customer data...
✅ Created 4 customers
📝 Seeding Staff data...
✅ Created 3 staff members
📝 Seeding Passenger data...
✅ Created 2 passengers
========================================
✨ Legacy Profile Module Ready!
📊 Database Summary:
   - Customers: 4
   - Staff: 3
   - Passengers: 2
========================================
```

## Sample Data (Pre-seeded)

### Customers

| IC Number      | Password | Name          | Email             |
| -------------- | -------- | ------------- | ----------------- |
| 040225-14-1143 | 12345678 | KY YAP        | kyyap@gmail.com   |
| 010604-04-0453 | 87654321 | Felicia Tee   | ftee@gmail.com    |
| 030710-10-4325 | 12344321 | Nicholas Chin | nicho@gmail.com   |
| 020312-11-6589 | 98761234 | Angela Ng     | aangela@gmail.com |

### Staff

| Staff ID | Password | Name        | Position           |
| -------- | -------- | ----------- | ------------------ |
| S001     | 11111    | Apple Doe   | Manager            |
| S002     | 22222    | Eric Lo     | Airline Controller |
| S003     | 33333    | Timothy Tan | Manager            |

### Passengers

| Passport  | Name         | Email              |
| --------- | ------------ | ------------------ |
| A12345678 | John Smith   | jsmith@gmail.com   |
| B98765432 | Mary Johnson | mjohnson@gmail.com |

## Testing the API

### Using cURL

**Test Customer Login:**

```bash
curl -X POST http://localhost:8081/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"icNumber":"040225-14-1143","password":"12345678"}'
```

**Test Staff Login:**

```bash
curl -X POST http://localhost:8081/api/staff/login \
  -H "Content-Type: application/json" \
  -d '{"staffId":"S001","password":11111}'
```

**Get All Customers:**

```bash
curl http://localhost:8081/api/customers
```

### Using Postman

1. Import the endpoints into Postman
2. Set base URL: `http://localhost:8081`
3. Test each endpoint with sample data

### Using Browser

- View customers: http://localhost:8081/api/customers
- View staff: http://localhost:8081/api/staff
- View passengers: http://localhost:8081/api/passengers

## Database Schema (Auto-created by JPA)

### Table: `customers`

```sql
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    ic_number VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL
);
```

### Table: `staff`

```sql
CREATE TABLE staff (
    id BIGSERIAL PRIMARY KEY,
    staff_id VARCHAR(10) UNIQUE NOT NULL,
    position VARCHAR(50) NOT NULL,
    password INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL
);
```

### Table: `passengers`

```sql
CREATE TABLE passengers (
    id BIGSERIAL PRIMARY KEY,
    passport_no VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL
);
```

## Legacy Anti-Patterns (Intentional for Modernization Demo)

### 🔴 Security Issues

1. **Plain Text Passwords**: Passwords stored without hashing
2. **Weak Password Policy**: Customer passwords only 8 characters
3. **Numeric Staff Passwords**: 5-digit numbers easily brute-forced
4. **No JWT/Token Authentication**: Session management missing

### 🔴 Architecture Issues

1. **No Service Layer**: Business logic in controllers
2. **No DTOs**: Entities exposed directly in API
3. **Validation in Entities**: Business rules scattered in domain objects
4. **Static Methods**: Legacy code uses static methods (not migrated)
5. **Console Output**: System.out.println in entity validation

### 🔴 Code Quality Issues

1. **God Classes**: Controllers too large with multiple responsibilities
2. **Tight Coupling**: Direct repository access in controllers
3. **No Exception Handling**: Generic try-catch blocks
4. **Code Duplication**: Helper methods repeated across controllers
5. **Magic Strings**: Hard-coded error messages

### 🔴 Testing Issues

1. **No Unit Tests**: Zero test coverage
2. **No Integration Tests**: API endpoints untested
3. **No Validation Tests**: Regex patterns not verified

## Modernization Opportunities (Assignment Phase 2)

This legacy code provides excellent material for:

1. **Refactoring to Clean Architecture**

   - Extract service layer
   - Create DTOs/ViewModels
   - Implement dependency injection properly

2. **Security Improvements**

   - Add Spring Security
   - Implement BCrypt password hashing
   - JWT token authentication
   - Role-based access control

3. **Validation Separation**

   - Create ValidationService
   - Use Bean Validation (@Valid, @NotNull, etc.)
   - Centralize validation logic

4. **Testing Implementation**

   - Unit tests for services
   - Integration tests for controllers
   - Repository tests with H2
   - Achieve >80% code coverage

5. **API Design Improvements**
   - RESTful best practices
   - Proper HTTP status codes
   - API versioning
   - OpenAPI/Swagger documentation

## Troubleshooting

### Cannot connect to Supabase

- Verify database credentials in `application.properties`
- Check Supabase project is active
- Ensure IP address is allowed (Supabase Settings → Database → Connection Pooling)

### Port 8081 already in use

```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <process_id> /F

# Linux/Mac
lsof -i :8081
kill -9 <process_id>
```

### Lombok not working

- Install Lombok plugin in your IDE
- Enable annotation processing (IntelliJ: Preferences → Build → Compiler → Annotation Processors)

### JPA errors

- Verify Java version is 17+
- Check `spring-boot-starter-data-jpa` in pom.xml
- Ensure PostgreSQL driver is included

## Project Structure

```
springboot/
├── src/
│   ├── main/
│   │   ├── java/com/example/springboot/
│   │   │   ├── controller/
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── StaffController.java
│   │   │   │   └── PassengerController.java
│   │   │   ├── entity/
│   │   │   │   ├── Person.java (abstract)
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Staff.java
│   │   │   │   └── Passenger.java
│   │   │   ├── repository/
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── StaffRepository.java
│   │   │   │   └── PassengerRepository.java
│   │   │   └── SpringbootApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/ (empty - for assignment phase 2)
├── pom.xml
└── README.md (this file)
```

## License

Educational project for Software Maintenance Assignment

## Contributors

- Original Legacy Code: KY YAP, ANG, nicho
- Spring Boot Migration: [Your Team Name]
- Assignment: Software Maintenance (Modernisation)
