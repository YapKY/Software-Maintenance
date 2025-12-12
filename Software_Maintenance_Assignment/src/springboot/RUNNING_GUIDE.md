# Running the Application - Quick Guide

## ✅ Build Successful!

The Spring Boot application with **MVC Architecture** has been successfully built!

---

## 🚀 How to Run the Application

### Option 1: Using Maven Wrapper (Recommended - No Maven Install Required)

```powershell
# Navigate to springboot folder
cd springboot

# Run the application
.\mvnw.cmd spring-boot:run
```

### Option 2: Using Java Directly

```powershell
cd springboot
java -jar target\springboot-0.0.1-SNAPSHOT.jar
```

---

## ⚙️ Before Running: Configure Supabase

The application is configured but needs your Supabase credentials.

### Step 1: Create Supabase Project

1. Go to https://supabase.com
2. Sign up / Log in
3. Create new project
4. Note your database password

### Step 2: Get Connection Details

In Supabase Dashboard → Settings → Database:

- **Host**: `db.your-project-ref.supabase.co`
- **Port**: `5432`
- **Database**: `postgres`
- **User**: `postgres`
- **Password**: Your database password

### Step 3: Update Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://db.YOUR-PROJECT-REF.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=YOUR-PASSWORD-HERE
```

**Example:**

```properties
spring.datasource.url=jdbc:postgresql://db.abcdefghijk.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=MySecurePassword123
```

---

## 🏗️ MVC Architecture Implementation

The system now implements **proper MVC architecture**:

### Model (Entity Layer)

- `Person.java` - Abstract base model
- `Customer.java` - Customer model
- `Staff.java` - Staff model
- `Passenger.java` - Passenger model

### View (JSON Responses)

- REST API returns JSON responses
- Consistent response format
- HTTP status codes (200, 201, 400, 404)

### Controller Layer

- `CustomerController.java` - Customer endpoints
- `StaffController.java` - Staff endpoints
- `PassengerController.java` - Passenger endpoints

### Service Layer (Business Logic)

- `CustomerService.java` - Customer business logic
- `StaffService.java` - Staff business logic

### Repository Layer (Data Access)

- `CustomerRepository.java` - Customer database operations
- `StaffRepository.java` - Staff database operations
- `PassengerRepository.java` - Passenger database operations

---

## 📊 Request Flow (MVC Pattern)

```
Client Request
    ↓
Controller (Handles HTTP request)
    ↓
Service (Business logic & validation)
    ↓
Repository (Database operations)
    ↓
Entity/Model (Data structure)
    ↓
Database (Supabase PostgreSQL)
    ↓
Response flows back up the chain
    ↓
Client receives JSON response (View)
```

---

## 🧪 Testing After Startup

Once running, you should see:

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
========================================
```

### Test Customer Login

```powershell
curl -X POST http://localhost:8081/api/customers/login `
  -H "Content-Type: application/json" `
  -d '{"icNumber":"040225-14-1143","password":"12345678"}'
```

### Test Staff Login

```powershell
curl -X POST http://localhost:8081/api/staff/login `
  -H "Content-Type: application/json" `
  -d '{"staffId":"S001","password":11111}'
```

### View All Customers

```powershell
curl http://localhost:8081/api/customers
```

Or open in browser:

- http://localhost:8081/api/customers
- http://localhost:8081/api/staff
- http://localhost:8081/api/passengers

---

## 📁 Project Structure (MVC)

```
springboot/
├── src/main/java/com/example/springboot/
│   ├── controller/          ← CONTROLLER (Handles HTTP requests)
│   │   ├── CustomerController.java
│   │   ├── StaffController.java
│   │   └── PassengerController.java
│   │
│   ├── service/             ← SERVICE (Business Logic)
│   │   ├── CustomerService.java
│   │   └── StaffService.java
│   │
│   ├── repository/          ← REPOSITORY (Data Access)
│   │   ├── CustomerRepository.java
│   │   ├── StaffRepository.java
│   │   └── PassengerRepository.java
│   │
│   ├── entity/              ← MODEL (Data Structure)
│   │   ├── Person.java
│   │   ├── Customer.java
│   │   ├── Staff.java
│   │   └── Passenger.java
│   │
│   └── SpringbootApplication.java
│
├── src/main/resources/
│   └── application.properties  ← Database configuration
│
├── pom.xml                     ← Maven dependencies
├── mvnw.cmd                    ← Maven wrapper (Windows)
└── mvnw                        ← Maven wrapper (Linux/Mac)
```

**VIEW Layer**: JSON responses returned by controllers

---

## 🔧 Troubleshooting

### Error: "mvn is not recognized"

**Solution**: Use Maven wrapper instead:

```powershell
.\mvnw.cmd spring-boot:run
```

### Error: "Cannot connect to database"

**Solution**:

1. Check Supabase project is active
2. Verify credentials in `application.properties`
3. Ensure port is 5432 (not 6543)
4. Add IP to Supabase allowlist if needed

### Error: "Port 8081 already in use"

**Solution**:

```powershell
# Find process using port 8081
netstat -ano | findstr :8081

# Kill the process
taskkill /PID <process_id> /F
```

---

## 📚 Documentation Files

- **README.md** - Complete project documentation
- **SETUP_GUIDE.md** - Detailed Supabase setup
- **MVC_ARCHITECTURE.md** - MVC pattern explanation
- **API_TESTING_GUIDE.md** - API testing examples
- **RUNNING_GUIDE.md** - This file

---

## ✨ Summary

### What's Ready:

✅ MVC Architecture implemented  
✅ Service layer for business logic  
✅ Controller layer for HTTP handling  
✅ Repository layer for data access  
✅ Entity/Model layer for data structure  
✅ Maven wrapper configured (no Maven install needed)  
✅ Spring Boot 4.0.0  
✅ PostgreSQL/Supabase integration  
✅ Sample data seeding  
✅ REST API endpoints (21 total)

### Next Steps:

1. Configure Supabase credentials
2. Run: `.\mvnw.cmd spring-boot:run`
3. Test API endpoints
4. Begin modernization phase (Phase 2)

---

**Application Port**: http://localhost:8081  
**API Base URL**: http://localhost:8081/api/

**Sample Login Credentials** (pre-seeded):

- Customer: IC `040225-14-1143`, Password `12345678`
- Staff: ID `S001`, Password `11111`

Happy coding! 🚀
