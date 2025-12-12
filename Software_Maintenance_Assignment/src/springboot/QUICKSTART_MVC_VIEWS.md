# Quick Start Guide - MVC View Layer Integration

## ✅ What Was Done

### 1. Added Thymeleaf Dependency

Added Spring Boot Thymeleaf starter to `pom.xml` for server-side template rendering.

### 2. Created View Controller

Created `ViewController.java` to handle page routing following MVC pattern:

- Separate from REST controllers
- Uses `@Controller` (not `@RestController`)
- Returns view names, not JSON
- Passes data to views via Spring `Model`

### 3. Moved HTML Files to Templates Directory

Moved HTML files from Java package to proper location:

- From: `src/main/java/com/example/springboot/view/*.html`
- To: `src/main/resources/templates/*.html`

### 4. Integrated Thymeleaf with Vue.js

Updated HTML templates to use:

- Thymeleaf namespace: `xmlns:th="http://www.thymeleaf.org"`
- Thymeleaf URLs: `th:href="@{/}"`
- Inline expressions: `/*[[${customerId}]]*/ 1` for server-side data injection

## 🚀 How to Run

### Step 1: Start Spring Boot Application

```bash
cd springboot
.\mvnw.cmd spring-boot:run
```

### Step 2: Access the Application

Open your browser and navigate to:

**Home Page:**

```
http://localhost:8081/
```

**Customer Profile Pages:**

```
http://localhost:8081/customer-profile
http://localhost:8081/customer-profile?id=2
http://localhost:8081/customer/1
```

**Staff Profile Pages:**

```
http://localhost:8081/staff-profile
http://localhost:8081/staff-profile?id=2
http://localhost:8081/staff/1
```

## 🎯 MVC Architecture Explained

### Request Flow for Page Load

1. **User Request:** Browser → `GET /customer-profile?id=2`
2. **View Controller:**

   ```java
   @Controller
   public class ViewController {
       @GetMapping("/customer-profile")
       public String customerProfile(@RequestParam Integer customerId, Model model) {
           model.addAttribute("customerId", customerId);
           return "customer-profile";  // View name
       }
   }
   ```

3. **Thymeleaf Engine:**

   - Finds template: `templates/customer-profile.html`
   - Injects data: Replaces `${customerId}` with actual value
   - Renders HTML: Generates final HTML with data

4. **Browser:** Receives rendered HTML with Vue.js app initialized

5. **Vue.js:** Makes AJAX call to REST API to fetch full customer data

### Request Flow for Data Operations

1. **Vue.js Event:** User clicks "Save Changes"

2. **AJAX Request:** `PUT /api/customers/2` with JSON data

3. **REST Controller:**

   ```java
   @RestController
   @RequestMapping("/api")
   public class CustomerController {
       @PutMapping("/customers/{id}")
       public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
           // Update logic
       }
   }
   ```

4. **Service Layer:** Business logic and validation

5. **Repository Layer:** Database operations

6. **Response:** JSON data back to Vue.js

7. **Vue.js:** Updates UI with new data

## 📁 File Structure

```
springboot/
├── src/main/
│   ├── java/com/example/springboot/
│   │   ├── controller/
│   │   │   ├── ViewController.java          ← NEW: Serves HTML pages
│   │   │   ├── CustomerController.java      ← REST API for customers
│   │   │   ├── StaffController.java         ← REST API for staff
│   │   │   └── PassengerController.java     ← REST API for passengers
│   │   ├── service/
│   │   │   ├── CustomerService.java         ← Business logic
│   │   │   └── StaffService.java
│   │   ├── repository/
│   │   │   ├── CustomerRepository.java      ← Data access
│   │   │   ├── StaffRepository.java
│   │   │   └── PassengerRepository.java
│   │   ├── model/
│   │   │   ├── Person.java                  ← Entities
│   │   │   ├── Customer.java
│   │   │   ├── Staff.java
│   │   │   └── Passenger.java
│   │   └── SpringbootApplication.java
│   └── resources/
│       ├── templates/                        ← NEW: Thymeleaf views
│       │   ├── index.html                   ← Landing page
│       │   ├── customer-profile.html        ← Customer profile UI
│       │   └── staff-profile.html           ← Staff profile UI
│       └── application.properties
└── pom.xml                                  ← Updated with Thymeleaf
```

## 🔄 URL Routing

### View Pages (HTML)

| URL                      | Controller     | Method                | View                  |
| ------------------------ | -------------- | --------------------- | --------------------- |
| `/`                      | ViewController | index()               | index.html            |
| `/customer-profile`      | ViewController | customerProfile()     | customer-profile.html |
| `/customer-profile?id=2` | ViewController | customerProfile()     | customer-profile.html |
| `/customer/{id}`         | ViewController | customerProfileById() | customer-profile.html |
| `/staff-profile`         | ViewController | staffProfile()        | staff-profile.html    |
| `/staff/{id}`            | ViewController | staffProfileById()    | staff-profile.html    |

### REST API Endpoints (JSON)

| Method | URL                    | Controller         | Purpose           |
| ------ | ---------------------- | ------------------ | ----------------- |
| GET    | `/api/customers/{id}`  | CustomerController | Get customer data |
| PUT    | `/api/customers/{id}`  | CustomerController | Update customer   |
| POST   | `/api/customers/login` | CustomerController | Login             |
| GET    | `/api/staff/{id}`      | StaffController    | Get staff data    |
| PUT    | `/api/staff/{id}`      | StaffController    | Update staff      |
| POST   | `/api/staff/login`     | StaffController    | Login             |

## 🧪 Testing the Integration

### 1. Test Home Page

```
http://localhost:8081/
```

✅ Should see landing page with two cards
✅ Clicking cards should navigate to profile pages

### 2. Test Customer Profile

```
http://localhost:8081/customer-profile
```

✅ Should load customer ID 1 (default)
✅ Should display customer name, email, phone, etc.
✅ Should allow editing and saving

### 3. Test Different Customer

```
http://localhost:8081/customer-profile?id=2
```

✅ Should load customer ID 2
✅ Can change ID in URL to load different customers (1-4)

### 4. Test Staff Profile

```
http://localhost:8081/staff-profile
```

✅ Should load staff ID 1 (default)
✅ Should display staff name, position, email, etc.
✅ Should allow editing and saving

### 5. Test Edit Functionality

1. Click "✏️ Edit Profile" button
2. Modify any field (name, email, phone)
3. Click "💾 Save Changes"
4. ✅ Should see success message
5. ✅ Data should be persisted to database

## 🎨 UI Features

### Landing Page (index.html)

- Clean gradient background
- Two profile cards (Customer & Staff)
- Responsive design
- Feature list

### Customer Profile (customer-profile.html)

- Blue/purple gradient theme
- View mode: Display all customer information
- Edit mode: Inline editing with validation
- AJAX-based data loading and updates
- Success/error messages
- Temporary ID selector (will be replaced with session)

### Staff Profile (staff-profile.html)

- Pink/red gradient theme (distinguishes from customer)
- View mode: Display staff information + position
- Edit mode: Inline editing including position
- Same AJAX functionality as customer profile

## 🔐 Current Authentication

**Temporary Implementation:**

- ID selector allows choosing which user to view
- Query parameter: `?id=X`
- Path variable: `/customer/{id}`

**Future Implementation:**

- Replace with session-based authentication
- Automatically load logged-in user's profile
- No manual ID selection needed

## ✨ Key Advantages

### 1. True MVC Architecture

- **Model:** Entity classes (Customer, Staff)
- **View:** Thymeleaf templates (HTML files)
- **Controller:** Separate controllers for views and APIs

### 2. Separation of Concerns

- **View Controller:** Handles page routing only
- **REST Controllers:** Handle API operations only
- **Service Layer:** Business logic
- **Repository Layer:** Data access

### 3. Server-Side Rendering + Client-Side Interactivity

- Thymeleaf renders initial page (SEO-friendly)
- Vue.js adds dynamic interactions
- Best of both worlds

### 4. Type Safety

- Spring Boot dependency injection
- Compile-time checking
- IDE support

### 5. Scalable

- Easy to add more views
- Easy to add more API endpoints
- Easy to add authentication

## 📖 Additional Documentation

For more details, see:

- [VIEW_LAYER_INTEGRATION.md](VIEW_LAYER_INTEGRATION.md) - Comprehensive architecture guide
- [MVC_ARCHITECTURE.md](MVC_ARCHITECTURE.md) - MVC pattern explanation
- [API_TESTING_GUIDE.md](API_TESTING_GUIDE.md) - API endpoint testing
- [RUNNING_GUIDE.md](RUNNING_GUIDE.md) - General running instructions

## 🐛 Troubleshooting

### Issue: Page shows "404 Not Found"

**Solution:** Ensure application is running on port 8081

### Issue: Page loads but no data

**Solution:**

1. Check if Supabase is configured in `application.properties`
2. Check if seeded data exists (IDs 1-4 for customers, 1-3 for staff)
3. Open browser console (F12) to see AJAX errors

### Issue: "Whitelabel Error Page"

**Solution:** Check ViewController mapping and template file names match

### Issue: Data not saving

**Solution:**

1. Check browser console for errors
2. Verify REST API is working: `GET http://localhost:8081/api/customers/1`
3. Check database connection in `application.properties`

## 🎉 Success Indicators

You'll know it's working when:

1. ✅ Home page loads at `http://localhost:8081/`
2. ✅ Clicking cards navigates to profile pages
3. ✅ Profile pages load with actual customer/staff data
4. ✅ Edit button enables form fields
5. ✅ Save button persists changes
6. ✅ Success message appears after saving
7. ✅ Refreshing page shows updated data

## 📝 Summary

The view layer is now fully integrated with Spring Boot using proper MVC architecture:

- ✅ **Thymeleaf** for server-side rendering
- ✅ **View Controller** for page routing
- ✅ **REST Controllers** for API operations
- ✅ **Vue.js** for reactive UI
- ✅ **Proper separation** between view and API layers
- ✅ **MVC pattern** correctly implemented
