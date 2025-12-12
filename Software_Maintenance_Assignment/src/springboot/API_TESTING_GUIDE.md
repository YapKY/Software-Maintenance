# API Testing Guide

## 🧪 Quick API Tests

### Prerequisites

- Application running on http://localhost:8081
- Tool: cURL (command line) or Postman/Thunder Client (GUI)

---

## Customer APIs

### 1. Customer Login ✅

**Endpoint**: POST `/api/customers/login`

```bash
curl -X POST http://localhost:8081/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"icNumber":"040225-14-1143","password":"12345678"}'
```

**Expected Response (Success)**:

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

**Test Wrong Password**:

```bash
curl -X POST http://localhost:8081/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"icNumber":"040225-14-1143","password":"wrongpass"}'
```

Expected: HTTP 401 Unauthorized

---

### 2. Customer Registration ✅

**Endpoint**: POST `/api/customers/register`

```bash
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "custIcNo": "990101-12-3456",
    "custPassword": "test1234",
    "name": "Test User",
    "email": "testuser@example.com",
    "phoneNumber": "012-3456789",
    "gender": "Male"
  }'
```

**Expected Response**:

```json
{
  "success": true,
  "message": "Registration Successful",
  "customer": {
    "id": 5,
    "icNumber": "990101-12-3456",
    "name": "Test User",
    "email": "testuser@example.com",
    "phoneNumber": "012-3456789",
    "gender": "Male"
  }
}
```

**Test Invalid IC Format**:

```bash
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "custIcNo": "12345",
    "custPassword": "test1234",
    "name": "Test User",
    "email": "test@example.com",
    "phoneNumber": "012-3456789",
    "gender": "Male"
  }'
```

Expected: HTTP 400 Bad Request - "Invalid I/C number format"

---

### 3. Get All Customers ✅

**Endpoint**: GET `/api/customers`

```bash
curl http://localhost:8081/api/customers
```

**Expected Response**: Array of all customers (4 pre-seeded)

---

### 4. Get Customer by ID ✅

**Endpoint**: GET `/api/customers/{id}`

```bash
curl http://localhost:8081/api/customers/1
```

---

### 5. Get Customer by IC Number ✅

**Endpoint**: GET `/api/customers/ic/{icNumber}`

```bash
curl http://localhost:8081/api/customers/ic/040225-14-1143
```

---

### 6. Update Customer Profile ✅

**Endpoint**: PUT `/api/customers/{id}`

```bash
curl -X PUT http://localhost:8081/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "KY YAP Updated",
    "phoneNumber": "011-9999999"
  }'
```

---

### 7. Delete Customer ✅

**Endpoint**: DELETE `/api/customers/{id}`

```bash
curl -X DELETE http://localhost:8081/api/customers/5
```

---

## Staff APIs

### 1. Staff Login ✅

**Endpoint**: POST `/api/staff/login`

```bash
curl -X POST http://localhost:8081/api/staff/login \
  -H "Content-Type: application/json" \
  -d '{"staffId":"S001","password":11111}'
```

**Expected Response**:

```json
{
  "success": true,
  "message": "Staff Login Successful",
  "staff": {
    "id": 1,
    "staffId": "S001",
    "position": "Manager",
    "name": "Apple Doe",
    "email": "apple@gmail.com",
    "phoneNumber": "018-9956348",
    "gender": "Female"
  }
}
```

**Test All Staff Accounts**:

```bash
# Staff 1 (Manager)
curl -X POST http://localhost:8081/api/staff/login \
  -H "Content-Type: application/json" \
  -d '{"staffId":"S001","password":11111}'

# Staff 2 (Airline Controller)
curl -X POST http://localhost:8081/api/staff/login \
  -H "Content-Type: application/json" \
  -d '{"staffId":"S002","password":22222}'

# Staff 3 (Manager)
curl -X POST http://localhost:8081/api/staff/login \
  -H "Content-Type: application/json" \
  -d '{"staffId":"S003","password":33333}'
```

---

### 2. Create Staff ✅

**Endpoint**: POST `/api/staff`

```bash
curl -X POST http://localhost:8081/api/staff \
  -H "Content-Type: application/json" \
  -d '{
    "staffId": "S004",
    "position": "Customer Service",
    "stfPass": 44444,
    "name": "New Staff",
    "email": "newstaff@example.com",
    "phoneNumber": "012-1234567",
    "gender": "Male"
  }'
```

---

### 3. Get All Staff ✅

**Endpoint**: GET `/api/staff`

```bash
curl http://localhost:8081/api/staff
```

---

### 4. Get Staff by Staff ID ✅

**Endpoint**: GET `/api/staff/staffid/{staffId}`

```bash
curl http://localhost:8081/api/staff/staffid/S001
```

---

## Passenger APIs

### 1. Create Passenger ✅

**Endpoint**: POST `/api/passengers`

```bash
curl -X POST http://localhost:8081/api/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "passportNo": "C12345678",
    "name": "Jane Doe",
    "email": "jane@example.com",
    "phoneNumber": "013-7654321",
    "gender": "Female"
  }'
```

**Test Invalid Passport Format**:

```bash
curl -X POST http://localhost:8081/api/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "passportNo": "12345678",
    "name": "Jane Doe",
    "email": "jane@example.com",
    "phoneNumber": "013-7654321",
    "gender": "Female"
  }'
```

Expected: HTTP 400 - "Invalid passport number format"

---

### 2. Get All Passengers ✅

**Endpoint**: GET `/api/passengers`

```bash
curl http://localhost:8081/api/passengers
```

---

### 3. Get Passenger by Passport ✅

**Endpoint**: GET `/api/passengers/passport/{passportNo}`

```bash
curl http://localhost:8081/api/passengers/passport/A12345678
```

---

## Validation Tests

### Test Customer Validations

#### 1. Invalid IC Number Format

```bash
# Missing dashes
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{"custIcNo":"123456121234","custPassword":"test1234","name":"Test","email":"test@example.com","phoneNumber":"012-3456789","gender":"Male"}'
```

#### 2. Password Too Short/Long

```bash
# Only 7 characters (needs exactly 8)
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{"custIcNo":"990101-12-3456","custPassword":"test123","name":"Test","email":"test@example.com","phoneNumber":"012-3456789","gender":"Male"}'
```

#### 3. Invalid Email Format

```bash
# Missing .com
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{"custIcNo":"990101-12-3456","custPassword":"test1234","name":"Test","email":"test@example","phoneNumber":"012-3456789","gender":"Male"}'
```

#### 4. Invalid Phone Number

```bash
# Missing dash
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{"custIcNo":"990101-12-3456","custPassword":"test1234","name":"Test","email":"test@example.com","phoneNumber":"0123456789","gender":"Male"}'
```

#### 5. Invalid Gender

```bash
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{"custIcNo":"990101-12-3456","custPassword":"test1234","name":"Test","email":"test@example.com","phoneNumber":"012-3456789","gender":"Other"}'
```

#### 6. Duplicate IC Number

```bash
# Try to register with existing IC
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{"custIcNo":"040225-14-1143","custPassword":"newpass12","name":"Another User","email":"another@example.com","phoneNumber":"012-9999999","gender":"Male"}'
```

Expected: HTTP 400 - "I/C number already exists"

---

## Test Staff Validations

### Invalid Staff Password (Not 5 digits)

```bash
curl -X POST http://localhost:8081/api/staff \
  -H "Content-Type: application/json" \
  -d '{"staffId":"S999","position":"Test","stfPass":123,"name":"Test Staff","email":"test@example.com","phoneNumber":"012-3456789","gender":"Male"}'
```

Expected: HTTP 400 - "Password must be a 5-digit number"

---

## Test Passenger Validations

### Invalid Passport Format

```bash
# Missing uppercase letter
curl -X POST http://localhost:8081/api/passengers \
  -H "Content-Type: application/json" \
  -d '{"passportNo":"a12345678","name":"Test","email":"test@example.com","phoneNumber":"012-3456789","gender":"Male"}'
```

Expected: HTTP 400 - "Invalid passport number format"

---

## Browser Testing (GET Endpoints)

Open in browser:

- http://localhost:8081/api/customers
- http://localhost:8081/api/staff
- http://localhost:8081/api/passengers
- http://localhost:8081/api/customers/1
- http://localhost:8081/api/staff/staffid/S001
- http://localhost:8081/api/passengers/passport/A12345678

---

## Postman Collection

### Import as JSON:

```json
{
  "info": {
    "name": "Airline Booking Legacy API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Customer Login",
      "request": {
        "method": "POST",
        "url": "http://localhost:8081/api/customers/login",
        "body": {
          "mode": "raw",
          "raw": "{\n  \"icNumber\": \"040225-14-1143\",\n  \"password\": \"12345678\"\n}",
          "options": { "raw": { "language": "json" } }
        }
      }
    },
    {
      "name": "Staff Login",
      "request": {
        "method": "POST",
        "url": "http://localhost:8081/api/staff/login",
        "body": {
          "mode": "raw",
          "raw": "{\n  \"staffId\": \"S001\",\n  \"password\": 11111\n}",
          "options": { "raw": { "language": "json" } }
        }
      }
    }
  ]
}
```

---

## Quick Test Script (Bash)

Save as `test_api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8081/api"

echo "=== Testing Customer Login ==="
curl -s -X POST $BASE_URL/customers/login \
  -H "Content-Type: application/json" \
  -d '{"icNumber":"040225-14-1143","password":"12345678"}' | jq

echo -e "\n=== Testing Staff Login ==="
curl -s -X POST $BASE_URL/staff/login \
  -H "Content-Type: application/json" \
  -d '{"staffId":"S001","password":11111}' | jq

echo -e "\n=== Getting All Customers ==="
curl -s $BASE_URL/customers | jq

echo -e "\n=== Getting All Staff ==="
curl -s $BASE_URL/staff | jq

echo -e "\n=== Getting All Passengers ==="
curl -s $BASE_URL/passengers | jq
```

Run: `bash test_api.sh`

---

## Expected Test Results Summary

| Test                        | Expected Result              |
| --------------------------- | ---------------------------- |
| Valid customer login        | HTTP 200, success: true      |
| Invalid customer login      | HTTP 401, success: false     |
| Valid customer registration | HTTP 201, customer created   |
| Duplicate IC registration   | HTTP 400, error message      |
| Invalid IC format           | HTTP 400, validation error   |
| Invalid password length     | HTTP 400, validation error   |
| Valid staff login           | HTTP 200, success: true      |
| Invalid staff login         | HTTP 401, success: false     |
| Get all customers           | HTTP 200, array with 4 items |
| Get all staff               | HTTP 200, array with 3 items |
| Get all passengers          | HTTP 200, array with 2 items |
| Valid passenger creation    | HTTP 201, passenger created  |
| Invalid passport format     | HTTP 400, validation error   |

---

## Troubleshooting API Tests

### Error: Connection refused

**Solution**: Ensure Spring Boot app is running on port 8081

### Error: 500 Internal Server Error

**Solution**: Check console logs for database connection issues

### Error: 404 Not Found

**Solution**: Verify endpoint URL is correct (check for typos)

### No response data

**Solution**: Add `-v` flag to cURL to see full response:

```bash
curl -v http://localhost:8081/api/customers
```

---

**Happy Testing! 🚀**
