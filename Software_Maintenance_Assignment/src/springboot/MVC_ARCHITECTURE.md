# MVC Architecture Implementation

## Overview

This Spring Boot application implements the **Model-View-Controller (MVC)** architectural pattern, which separates the application into three interconnected components:

```
┌─────────────────────────────────────────────────────┐
│                      CLIENT                         │
│            (Web Browser / API Client)               │
└───────────────────────┬─────────────────────────────┘
                        │ HTTP Requests
                        ▼
┌─────────────────────────────────────────────────────┐
│                    CONTROLLER                       │
│            (CustomerController.java)                │
│  - Handles HTTP requests                            │
│  - Routes to appropriate services                   │
│  - Returns JSON responses (View)                    │
└───────────────────────┬─────────────────────────────┘
                        │ Calls service methods
                        ▼
┌─────────────────────────────────────────────────────┐
│                     SERVICE                         │
│             (CustomerService.java)                  │
│  - Business logic layer                             │
│  - Validation                                       │
│  - Data processing                                  │
└───────────────────────┬─────────────────────────────┘
                        │ Uses repository
                        ▼
┌─────────────────────────────────────────────────────┐
│                   REPOSITORY                        │
│          (CustomerRepository.java)                  │
│  - Data access layer                                │
│  - Database operations (CRUD)                       │
└───────────────────────┬─────────────────────────────┘
                        │ Manages entities
                        ▼
┌─────────────────────────────────────────────────────┐
│                     MODEL                           │
│              (Customer.java Entity)                 │
│  - Data structure                                   │
│  - Domain objects                                   │
└───────────────────────┬─────────────────────────────┘
                        │ Persisted to
                        ▼
┌─────────────────────────────────────────────────────┐
│                   DATABASE                          │
│            (Supabase PostgreSQL)                    │
│  - customers, staff, passengers tables              │
└─────────────────────────────────────────────────────┘
```

---

## MVC Components in Detail

### 1. MODEL Layer

**Purpose**: Represents the data and business entities

**Location**: `src/main/java/com/example/springboot/entity/`

**Files**:

- `Person.java` - Abstract base class with common attributes
- `Customer.java` - Customer entity (extends Person)
- `Staff.java` - Staff entity (extends Person)
- `Passenger.java` - Passenger entity (extends Person)

**Responsibilities**:

- ✅ Define data structure (fields/attributes)
- ✅ Map to database tables using JPA annotations
- ✅ Provide getters and setters
- ✅ Basic validation methods (legacy style)

**Example - Customer Model**:

```java
@Entity
@Table(name = "customers")
public class Customer extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String custIcNo;

    @Column(nullable = false)
    private String custPassword;
    // ... other fields
}
```

---

### 2. VIEW Layer

**Purpose**: Presentation layer - how data is displayed to users

**Implementation**: **JSON REST API Responses** (RESTful Web Services)

In modern REST APIs, the "View" is the **JSON response format** sent to clients, not traditional HTML pages.

**Example Response (JSON View)**:

```json
{
  "success": true,
  "message": "Log In Successful",
  "customer": {
    "id": 1,
    "icNumber": "040225-14-1143",
    "name": "KY YAP",
    "email": "kyyap@gmail.com",
    "phoneNumber": "011-0818007",
    "gender": "Male"
  }
}
```

**View Responsibilities**:

- ✅ Format data as JSON
- ✅ Provide consistent response structure
- ✅ HTTP status codes (200, 201, 400, 404, etc.)
- ✅ Error messages

---

### 3. CONTROLLER Layer

**Purpose**: Handles user requests and coordinates between Model and View

**Location**: `src/main/java/com/example/springboot/controller/`

**Files**:

- `CustomerController.java` - Customer REST endpoints
- `StaffController.java` - Staff REST endpoints
- `PassengerController.java` - Passenger REST endpoints

**Responsibilities**:

- ✅ Receive HTTP requests (GET, POST, PUT, DELETE)
- ✅ Extract request parameters
- ✅ Call appropriate service methods
- ✅ Handle exceptions
- ✅ Return formatted responses (View)

**Example - Customer Login Endpoint**:

```java
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;  // Delegates to service layer

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String icNumber = credentials.get("icNumber");
        String password = credentials.get("password");

        // Call service layer (business logic)
        Optional<Customer> customer = customerService.authenticateCustomer(icNumber, password);

        if (customer.isPresent()) {
            // Return JSON view
            return ResponseEntity.ok(createSuccessResponse(customer.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid credentials"));
        }
    }
}
```

---

## Additional Layers (Enterprise MVC)

### 4. SERVICE Layer

**Purpose**: Business logic and transaction management

**Location**: `src/main/java/com/example/springboot/service/`

**Files**:

- `CustomerService.java` - Customer business logic
- `StaffService.java` - Staff business logic

**Responsibilities**:

- ✅ Implement business rules
- ✅ Validation logic
- ✅ Coordinate multiple repository calls
- ✅ Transaction management
- ✅ Keep controllers thin

**Example**:

```java
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Customer> authenticateCustomer(String icNumber, String password) {
        Optional<Customer> customer = customerRepository.findByCustIcNo(icNumber);

        // Business logic: password comparison
        if (customer.isPresent() && customer.get().getCustPassword().equals(password)) {
            return customer;
        }
        return Optional.empty();
    }

    public Customer registerCustomer(Customer customer) {
        // Business logic: validation
        if (!customer.getValidICNumber(customer.getCustIcNo())) {
            throw new IllegalArgumentException("Invalid IC format");
        }
        // ... more validation

        return customerRepository.save(customer);
    }
}
```

---

### 5. REPOSITORY Layer

**Purpose**: Data access abstraction

**Location**: `src/main/java/com/example/springboot/repository/`

**Files**:

- `CustomerRepository.java`
- `StaffRepository.java`
- `PassengerRepository.java`

**Responsibilities**:

- ✅ Database CRUD operations
- ✅ Custom queries
- ✅ Abstracts SQL from business logic

**Example**:

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustIcNo(String custIcNo);
    Optional<Customer> findByEmail(String email);
    boolean existsByCustIcNo(String custIcNo);
}
```

---

## MVC Request Flow Example

### Login Request Flow

```
1. CLIENT sends HTTP POST request:
   POST http://localhost:8081/api/customers/login
   Body: {"icNumber":"040225-14-1143", "password":"12345678"}

   ↓

2. CONTROLLER (CustomerController.java) receives request
   - Extracts icNumber and password from request body
   - Calls service layer

   ↓

3. SERVICE (CustomerService.java) processes business logic
   - Calls repository to find customer by IC number
   - Validates password matches
   - Returns customer if valid

   ↓

4. REPOSITORY (CustomerRepository.java) accesses database
   - Executes SQL query: SELECT * FROM customers WHERE ic_number = ?
   - Returns Customer entity

   ↓

5. MODEL (Customer.java) represents the data
   - Customer object with id, name, email, etc.

   ↓

6. CONTROLLER formats the VIEW (JSON response)
   - Creates success/error response object
   - Sets HTTP status code

   ↓

7. CLIENT receives JSON response (VIEW):
   {
     "success": true,
     "message": "Log In Successful",
     "customer": { ... }
   }
```

---

## MVC Architecture Diagram

### Traditional Web MVC vs REST API MVC

#### Traditional Web MVC (HTML Pages)

```
Browser → Controller → Service → Repository → Database
    ↑
    └─── HTML View (JSP/Thymeleaf) ───────┘
```

#### REST API MVC (This Project)

```
API Client → Controller → Service → Repository → Database
    ↑
    └─── JSON Response (View) ────────────┘
```

---

## File Structure by MVC Layer

```
springboot/
└── src/main/java/com/example/springboot/
    │
    ├── entity/              ← MODEL Layer
    │   ├── Person.java      (Abstract base model)
    │   ├── Customer.java    (Customer model)
    │   ├── Staff.java       (Staff model)
    │   └── Passenger.java   (Passenger model)
    │
    ├── repository/          ← DATA ACCESS Layer
    │   ├── CustomerRepository.java
    │   ├── StaffRepository.java
    │   └── PassengerRepository.java
    │
    ├── service/             ← BUSINESS LOGIC Layer
    │   ├── CustomerService.java
    │   └── StaffService.java
    │
    ├── controller/          ← CONTROLLER Layer
    │   ├── CustomerController.java
    │   ├── StaffController.java
    │   └── PassengerController.java
    │
    └── SpringbootApplication.java  (Main entry point)
```

**VIEW Layer**: JSON responses returned by controllers

---

## Benefits of MVC Architecture

### 1. Separation of Concerns

- ✅ Each layer has a specific responsibility
- ✅ Changes in one layer don't affect others
- ✅ Easier to understand and maintain

### 2. Reusability

- ✅ Service methods can be called by multiple controllers
- ✅ Models can be used across different views
- ✅ Repositories can be injected anywhere

### 3. Testability

- ✅ Each layer can be tested independently
- ✅ Mock services in controller tests
- ✅ Mock repositories in service tests

### 4. Scalability

- ✅ Can add new controllers without touching services
- ✅ Can add new services without touching repositories
- ✅ Easy to add new features

---

## API Endpoints by MVC Pattern

### Customer Module

| HTTP Method | Endpoint                  | Controller Method   | Service Method           | View (Response)           |
| ----------- | ------------------------- | ------------------- | ------------------------ | ------------------------- |
| POST        | `/api/customers/login`    | `login()`           | `authenticateCustomer()` | JSON with customer data   |
| POST        | `/api/customers/register` | `register()`        | `registerCustomer()`     | JSON with success message |
| GET         | `/api/customers`          | `getAllCustomers()` | `getAllCustomers()`      | JSON array of customers   |
| GET         | `/api/customers/{id}`     | `getCustomerById()` | `getCustomerById()`      | JSON customer object      |
| PUT         | `/api/customers/{id}`     | `updateCustomer()`  | `updateCustomer()`       | JSON updated customer     |
| DELETE      | `/api/customers/{id}`     | `deleteCustomer()`  | `deleteCustomer()`       | JSON success message      |

---

## Testing MVC Layers

### Controller Testing (Integration Test)

```java
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(post("/api/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"icNumber\":\"040225-14-1143\",\"password\":\"12345678\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
```

### Service Testing (Unit Test)

```java
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testAuthenticateCustomer() {
        Customer customer = new Customer(...);
        when(customerRepository.findByCustIcNo(anyString()))
            .thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService
            .authenticateCustomer("040225-14-1143", "12345678");

        assertTrue(result.isPresent());
    }
}
```

---

## Summary

### MVC Pattern in This Project

| Layer          | Location       | Purpose           | Technologies     |
| -------------- | -------------- | ----------------- | ---------------- |
| **Model**      | `entity/`      | Data structure    | JPA, Hibernate   |
| **View**       | JSON responses | Data presentation | Jackson JSON     |
| **Controller** | `controller/`  | Request handling  | Spring Web, REST |
| **Service**    | `service/`     | Business logic    | Spring @Service  |
| **Repository** | `repository/`  | Data access       | Spring Data JPA  |

### Key Spring Boot Annotations

- `@RestController` - Marks controller class (Controller layer)
- `@Service` - Marks service class (Business logic layer)
- `@Repository` - Marks repository interface (Data access layer)
- `@Entity` - Marks model class (Model layer)
- `@RequestMapping` - Maps URLs to controller methods
- `@GetMapping`, `@PostMapping`, etc. - HTTP method mapping

---

## Next Steps for Modernization

1. **DTO Pattern**: Create separate DTOs for requests/responses
2. **Validation Layer**: Use Bean Validation (`@Valid`, `@NotNull`)
3. **Exception Handling**: Global exception handler with `@ControllerAdvice`
4. **Security Layer**: Add Spring Security with JWT
5. **Testing**: Unit tests for all layers (target: 80%+ coverage)

---

**This application follows the MVC architectural pattern with additional Service and Repository layers for enterprise-grade separation of concerns.**
