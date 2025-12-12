# View Layer Integration with Spring Boot MVC

## Overview

This document explains how the view layer (HTML pages) is connected to Spring Boot following the MVC (Model-View-Controller) architecture pattern.

## MVC Architecture Components

### 1. Model Layer

**Location:** `src/main/java/com/example/springboot/model/`

- `Person.java` - Abstract base entity
- `Customer.java` - Customer entity with profile data
- `Staff.java` - Staff entity with profile data
- `Passenger.java` - Passenger entity

These entities represent the data structure and are persisted to the database via JPA/Hibernate.

### 2. View Layer

**Location:** `src/main/resources/templates/`

- `index.html` - Landing page with navigation
- `customer-profile.html` - Customer profile page
- `staff-profile.html` - Staff profile page

**Technology:** Thymeleaf + Vue.js 3

- **Thymeleaf**: Server-side template engine for initial page rendering
- **Vue.js**: Client-side framework for dynamic UI interactions

### 3. Controller Layer

#### a) View Controllers

**Location:** `src/main/java/com/example/springboot/controller/ViewController.java`

Handles page rendering and routes:

- `GET /` → Returns `index.html`
- `GET /customer-profile` → Returns `customer-profile.html` with customerId
- `GET /staff-profile` → Returns `staff-profile.html` with staffId
- `GET /customer/{id}` → Returns customer profile by path variable
- `GET /staff/{id}` → Returns staff profile by path variable

**Annotations:**

- `@Controller` - Indicates this handles view rendering (not REST)
- `@GetMapping` - Maps HTTP GET requests to handler methods
- `Model` - Spring's model object for passing data to views

#### b) REST API Controllers

**Location:** `src/main/java/com/example/springboot/controller/`

- `CustomerController.java` - REST API for customer operations
- `StaffController.java` - REST API for staff operations
- `PassengerController.java` - REST API for passenger operations

**Annotations:**

- `@RestController` - Handles AJAX requests and returns JSON
- `@RequestMapping("/api/...")` - Maps REST endpoints

### 4. Service Layer

**Location:** `src/main/java/com/example/springboot/service/`

- `CustomerService.java` - Business logic for customers
- `StaffService.java` - Business logic for staff

Contains validation, authentication, and CRUD operations.

### 5. Repository Layer

**Location:** `src/main/java/com/example/springboot/repository/`

- `CustomerRepository.java` - Data access for customers
- `StaffRepository.java` - Data access for staff
- `PassengerRepository.java` - Data access for passengers

Spring Data JPA interfaces for database operations.

## Request Flow Diagram

### Page Load Flow (Server-Side Rendering)

```
User Browser
    ↓
GET /customer-profile
    ↓
ViewController.customerProfile()
    ↓
Add customerId to Model
    ↓
Return "customer-profile"
    ↓
Thymeleaf renders template
    ↓
HTML + Injected Data → Browser
    ↓
Vue.js initializes with data
```

### Data Update Flow (AJAX)

```
Vue.js Component
    ↓
axios.put('/api/customers/{id}')
    ↓
CustomerController.updateCustomer()
    ↓
CustomerService.updateCustomer()
    ↓
CustomerRepository.save()
    ↓
Database (Supabase PostgreSQL)
    ↓
JSON Response → Vue.js
    ↓
UI Updates
```

## Technology Stack Integration

### 1. Thymeleaf Integration

**Dependency Added to pom.xml:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

**Configuration (Default):**

- Template location: `src/main/resources/templates/`
- File extension: `.html`
- Template mode: HTML5

**Thymeleaf Syntax Used:**

#### HTML Declaration

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org"></html>
```

#### URL Generation

```html
<a th:href="@{/customer-profile}">Customer Profile</a>
```

- Generates context-aware URLs
- Automatically adds context path if needed

#### Inline Expressions

```javascript
customerId: /*[[${customerId}]]*/ 1;
```

- `/*[[...]]*/` - Thymeleaf inline expression
- Server-side: Replaced with actual value from Model
- Client-side: Falls back to default value (1) for IDE support

### 2. Vue.js Integration

**CDN Loaded:**

```html
<script src="https://cdn.jsdelivr.net/npm/vue@3/dist/vue.global.js"></script>
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
```

**Vue App Structure:**

```javascript
const { createApp } = Vue;

createApp({
  data() {
    return {
      customerId: /*[[${customerId}]]*/ 1, // From Thymeleaf
      customer: null,
      loading: false,
      error: null,
      editMode: false,
    };
  },
  mounted() {
    this.loadCustomer(); // Load data via AJAX
  },
  methods: {
    async loadCustomer() {
      const response = await axios.get(`/api/customers/${this.customerId}`);
      this.customer = response.data.customer || response.data;
    },
    async saveProfile() {
      await axios.put(`/api/customers/${this.customer.id}`, this.editForm);
    },
  },
}).mount("#app");
```

## URL Routing

### View Pages (Thymeleaf-rendered)

| URL                                           | Controller Method                      | View Template           | Description        |
| --------------------------------------------- | -------------------------------------- | ----------------------- | ------------------ |
| `http://localhost:8081/`                      | `ViewController.index()`               | `index.html`            | Home page          |
| `http://localhost:8081/customer-profile`      | `ViewController.customerProfile()`     | `customer-profile.html` | Customer profile   |
| `http://localhost:8081/customer-profile?id=2` | `ViewController.customerProfile()`     | `customer-profile.html` | Customer with ID=2 |
| `http://localhost:8081/customer/2`            | `ViewController.customerProfileById()` | `customer-profile.html` | Alternative URL    |
| `http://localhost:8081/staff-profile`         | `ViewController.staffProfile()`        | `staff-profile.html`    | Staff profile      |
| `http://localhost:8081/staff/1`               | `ViewController.staffProfileById()`    | `staff-profile.html`    | Staff with ID=1    |

### REST API Endpoints (JSON responses)

| Method | URL                    | Controller Method                      | Description           |
| ------ | ---------------------- | -------------------------------------- | --------------------- |
| GET    | `/api/customers/{id}`  | `CustomerController.getCustomerById()` | Get customer data     |
| PUT    | `/api/customers/{id}`  | `CustomerController.updateCustomer()`  | Update customer       |
| POST   | `/api/customers/login` | `CustomerController.login()`           | Authenticate customer |
| GET    | `/api/staff/{id}`      | `StaffController.getStaffById()`       | Get staff data        |
| PUT    | `/api/staff/{id}`      | `StaffController.updateStaff()`        | Update staff          |
| POST   | `/api/staff/login`     | `StaffController.login()`              | Authenticate staff    |

## Data Flow Example

### Example: Loading Customer Profile

1. **User navigates to:** `http://localhost:8081/customer-profile?id=2`

2. **ViewController processes request:**

```java
@GetMapping("/customer-profile")
public String customerProfile(
        @RequestParam(value = "id", required = false, defaultValue = "1") Integer customerId,
        Model model) {
    model.addAttribute("customerId", customerId);
    return "customer-profile";
}
```

3. **Thymeleaf renders template:**

- Replaces `/*[[${customerId}]]*/` with actual value `2`
- Generates proper URLs using `th:href="@{/}"`
- Sends rendered HTML to browser

4. **Vue.js initializes:**

```javascript
data() {
    return {
        customerId: 2,  // Now has actual value from server
        // ...
    }
},
mounted() {
    this.loadCustomer();  // Auto-loads customer ID 2
}
```

5. **AJAX call to REST API:**

```javascript
async loadCustomer() {
    const response = await axios.get(`http://localhost:8081/api/customers/2`);
    this.customer = response.data.customer;
}
```

6. **REST Controller returns data:**

```java
@GetMapping("/{id}")
public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Long id) {
    Optional<Customer> customer = customerService.getCustomerById(id);
    return ResponseEntity.ok(Map.of("customer", customer.get()));
}
```

7. **Vue.js updates UI** with customer data

## Benefits of This Architecture

### ✅ Separation of Concerns

- **View Controllers**: Handle page routing and initial data
- **REST Controllers**: Handle AJAX operations and return JSON
- **Services**: Contain business logic
- **Repositories**: Handle data access

### ✅ SEO-Friendly

- Server-side rendering with Thymeleaf
- Pages load with initial content (not blank while JavaScript loads)

### ✅ Progressive Enhancement

- Works without JavaScript (basic page load)
- Enhanced with Vue.js for rich interactions

### ✅ Maintainable

- Clear boundaries between layers
- Easy to test each component independently
- Changes in one layer don't affect others

### ✅ Scalable

- Can add more views without touching REST API
- Can add more API endpoints without touching views
- Service layer can be reused across controllers

## File Structure

```
src/
├── main/
│   ├── java/com/example/springboot/
│   │   ├── controller/
│   │   │   ├── ViewController.java          ← View routing
│   │   │   ├── CustomerController.java      ← REST API
│   │   │   ├── StaffController.java         ← REST API
│   │   │   └── PassengerController.java     ← REST API
│   │   ├── service/
│   │   │   ├── CustomerService.java         ← Business logic
│   │   │   └── StaffService.java            ← Business logic
│   │   ├── repository/
│   │   │   ├── CustomerRepository.java      ← Data access
│   │   │   ├── StaffRepository.java         ← Data access
│   │   │   └── PassengerRepository.java     ← Data access
│   │   ├── model/
│   │   │   ├── Person.java                  ← Entity
│   │   │   ├── Customer.java                ← Entity
│   │   │   ├── Staff.java                   ← Entity
│   │   │   └── Passenger.java               ← Entity
│   │   └── SpringbootApplication.java       ← Main class
│   └── resources/
│       ├── templates/                        ← Thymeleaf views
│       │   ├── index.html
│       │   ├── customer-profile.html
│       │   └── staff-profile.html
│       └── application.properties            ← Configuration
```

## Testing the Integration

### 1. Start the Application

```bash
cd springboot
.\mvnw.cmd spring-boot:run
```

### 2. Access Pages

- **Home:** http://localhost:8081/
- **Customer Profile (ID=1):** http://localhost:8081/customer-profile
- **Customer Profile (ID=2):** http://localhost:8081/customer-profile?id=2
- **Staff Profile (ID=1):** http://localhost:8081/staff-profile

### 3. Verify MVC Flow

1. Open browser developer tools (F12)
2. Navigate to customer profile page
3. Check Network tab:
   - Initial page load: `GET /customer-profile` → HTML response
   - Data load: `GET /api/customers/1` → JSON response
4. Edit profile and save
5. Check Network tab:
   - Update request: `PUT /api/customers/1` → JSON request/response

## Future Enhancements

### Phase 2 - Authentication Integration

Replace temporary ID selector with session-based authentication:

```java
@GetMapping("/customer-profile")
public String customerProfile(Authentication authentication, Model model) {
    UserDetails user = (UserDetails) authentication.getPrincipal();
    Integer customerId = getUserIdFromSession(user);
    model.addAttribute("customerId", customerId);
    return "customer-profile";
}
```

### Phase 3 - Add More Views

- Login page
- Registration page
- Dashboard
- Booking management

### Phase 4 - Enhance View Layer

- Add form validation
- Add loading states
- Add error handling
- Add success notifications

## Troubleshooting

### Issue: 404 Not Found for pages

**Cause:** Thymeleaf templates not in correct location
**Solution:** Ensure HTML files are in `src/main/resources/templates/`

### Issue: customerId not injecting

**Cause:** Missing Thymeleaf namespace or incorrect syntax
**Solution:** Verify `xmlns:th="http://www.thymeleaf.org"` in `<html>` tag

### Issue: REST API returning 404

**Cause:** Different endpoints for views and API
**Solution:** View pages use `/customer-profile`, API uses `/api/customers/{id}`

### Issue: CORS errors

**Cause:** Frontend and backend on different domains
**Solution:** Using Thymeleaf served from same domain (port 8081)

## Summary

This implementation successfully connects Spring Boot to the view layer using:

- ✅ **Thymeleaf** for server-side rendering (MVC pattern)
- ✅ **View Controllers** for page routing
- ✅ **REST Controllers** for AJAX operations
- ✅ **Vue.js** for reactive UI
- ✅ **Axios** for HTTP requests
- ✅ **Proper MVC architecture** with clear separation

The system now follows proper MVC architecture with the view layer fully integrated into Spring Boot.
