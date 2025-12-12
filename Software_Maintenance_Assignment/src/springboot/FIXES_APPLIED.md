# Fixes Applied - Profile View Data Loading Issues

## 🐛 Issues Identified

### 1. **Critical: Wrong Port Configuration**

- **Problem**: `server.port=0` caused Spring Boot to use a random port
- **Fix**: Changed to `server.port=8081` in `application.properties`
- **Impact**: API endpoints now consistently available at port 8081

### 2. **Customer Profile Field Mismatch**

- **Problem**: API returned `icNumber` but frontend expected `custIcNo`
- **Fix**: Updated `CustomerController.java` line 154 to use `custIcNo`
- **Impact**: Customer IC number now displays correctly

### 3. **Navigation Links Not Working**

- **Problem**: Only Thymeleaf `th:href` attributes without fallback `href`
- **Fix**: Added fallback `href` attributes to all navigation links in:
  - `index.html`
  - `customer-profile.html`
  - `staff-profile.html`
- **Impact**: Navigation works reliably even if Thymeleaf processing fails

---

## ✅ How to Run the Application

### Step 1: Stop All Java Processes

```powershell
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
```

### Step 2: Navigate to Spring Boot Directory

```powershell
cd "C:\Users\user\Desktop\Github\Software-Maintenance\Software_Maintenance_Assignment\src\springboot"
```

### Step 3: Start Spring Boot Application

```powershell
./mvnw spring-boot:run
```

### Step 4: Wait for Startup

Look for this message in the console:

```
Tomcat started on port 8081 (http)
Started SpringbootApplication in X.XXX seconds
```

---

## 🌐 How to Access the Application

### ⚠️ IMPORTANT: Do NOT use Live Server (port 5500)

**WRONG** ❌

- `http://127.0.0.1:5500/springboot/src/main/resources/templates/index.html`
- Opening HTML files directly
- Using any file server

**CORRECT** ✅

- `http://localhost:8081/` - Home page
- `http://localhost:8081/customer-profile` - Customer profile
- `http://localhost:8081/staff-profile` - Staff profile

---

## 🧪 Testing Steps

### 1. Test Home Page

1. Open browser
2. Navigate to: `http://localhost:8081/`
3. You should see the landing page with two profile cards

### 2. Test Customer Profile

1. Click "Customer Profile" button or navigate to `http://localhost:8081/customer-profile`
2. Enter ID: `1`
3. Click "Load Profile"
4. **Expected Result**: Customer details load, including IC number `040225-14-1143`

### 3. Test Staff Profile

1. Click "Staff Profile" button or navigate to `http://localhost:8081/staff-profile`
2. Enter ID: `1`
3. Click "Load Profile"
4. **Expected Result**: Staff details load, including staff ID `S001`

### 4. Test Navigation

1. Click "Back to Home" - should return to home page
2. Click "Customer Profile →" from staff page - should navigate to customer profile
3. Click "Staff Profile →" from customer page - should navigate to staff profile

---

## 📊 Available Test Data

### Customers (IDs 1-4)

| ID  | Name          | IC Number      | Email             |
| --- | ------------- | -------------- | ----------------- |
| 1   | KY YAP        | 040225-14-1143 | kyyap@gmail.com   |
| 2   | Felicia Tee   | 010604-04-0453 | ftee@gmail.com    |
| 3   | Nicholas Chin | 030710-10-4325 | nicho@gmail.com   |
| 4   | Angela Ng     | 020312-11-6589 | aangela@gmail.com |

### Staff (IDs 1-3)

| ID  | Name        | Staff ID | Position           |
| --- | ----------- | -------- | ------------------ |
| 1   | Apple Doe   | S001     | Manager            |
| 2   | Eric Lo     | S002     | Airline Controller |
| 3   | Timothy Tan | S003     | Manager            |

---

## 🔧 Troubleshooting

### Issue: "Cannot GET /customer-profile"

**Cause**: Accessing HTML files directly instead of through Spring Boot
**Solution**: Make sure you're accessing `http://localhost:8081/customer-profile`, not a file:// URL

### Issue: "Failed to load customer profile"

**Causes**:

1. Spring Boot not running
2. Accessing through wrong port
3. Database connection issue

**Solutions**:

1. Check Spring Boot is running: `netstat -ano | findstr "8081"`
2. Verify URL is `http://localhost:8081/`
3. Check console for database errors

### Issue: Port 8081 already in use

**Solution**:

```powershell
# Find what's using port 8081
netstat -ano | findstr "8081"

# Kill the process (replace XXXX with PID from above command)
taskkill /F /PID XXXX
```

### Issue: Navigation links don't work

**Cause**: Not accessing through Spring Boot server
**Solution**: Always use `http://localhost:8081/` URLs

---

## 📁 Files Modified

1. `src/main/resources/application.properties`

   - Changed `server.port=0` to `server.port=8081`

2. `src/main/java/com/example/springboot/controller/CustomerController.java`

   - Line 154: Changed `response.put("icNumber", ...)` to `response.put("custIcNo", ...)`

3. `src/main/resources/templates/index.html`

   - Added `href` fallback to all Thymeleaf links

4. `src/main/resources/templates/customer-profile.html`

   - Added `href` fallback to navigation buttons

5. `src/main/resources/templates/staff-profile.html`
   - Added `href` fallback to navigation buttons

---

## ✨ What Should Work Now

✅ Spring Boot runs on consistent port 8081
✅ Customer profile displays all data including IC number
✅ Staff profile displays all data correctly
✅ Navigation between pages works
✅ "Back to Home" buttons work
✅ API endpoints return correct data structure
✅ Thymeleaf templates process correctly

---

## 📞 Quick Reference

**Start Application**:

```powershell
cd "C:\Users\user\Desktop\Github\Software-Maintenance\Software_Maintenance_Assignment\src\springboot"
./mvnw spring-boot:run
```

**Access Application**:

- Home: http://localhost:8081/
- Customer: http://localhost:8081/customer-profile
- Staff: http://localhost:8081/staff-profile

**Stop Application**:
Press `Ctrl+C` in the terminal where Spring Boot is running

**Check if Running**:

```powershell
netstat -ano | findstr "8081"
```

---

Generated: 2025-12-12
