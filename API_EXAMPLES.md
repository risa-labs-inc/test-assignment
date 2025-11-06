# 📝 API Examples & Test Scenarios

This document provides copy-paste ready examples for testing the Book Library API.

**Before using these examples:**
1. Make sure the API is running (see `SETUP_GUIDE.md`)
2. API runs on `http://localhost:3000`
3. For Postman collection, see `book-library-api-mock/POSTMAN_COLLECTION.json`

## Table of Contents
- [Setup](#setup)
- [Authentication Examples](#authentication-examples)
- [Books - Read Operations](#books---read-operations)
- [Books - Create Operations](#books---create-operations)
- [Books - Update Operations](#books---update-operations)
- [Books - Delete Operations](#books---delete-operations)
- [Error Scenarios](#error-scenarios)
- [Test Data](#test-data)

---

## Setup

```bash
# Set base URL
export BASE_URL="http://localhost:3000"

# Function to get token
get_token() {
  curl -s -X POST $BASE_URL/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"test123"}' \
    | jq -r '.token'
}

# Get and store token
export TOKEN=$(get_token)
echo "Token: $TOKEN"
```

---

## Authentication Examples

### ✅ Valid Login
```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "test123"
  }' | jq
```

**Expected Response (200):**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": "24h",
  "user": {
    "username": "admin",
    "role": "admin"
  }
}
```

### ❌ Invalid Login
```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wrong",
    "password": "wrong"
  }' | jq
```

**Expected Response (401):**
```json
{
  "error": "Unauthorized",
  "message": "Invalid username or password"
}
```

### ❌ Missing Credentials
```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{}' | jq
```

**Expected Response (400):**
```json
{
  "error": "Bad Request",
  "message": "Username and password are required"
}
```

---

## Books - Read Operations

### ✅ Get All Books
```bash
curl http://localhost:3000/books | jq
```

**Expected Response (200):**
```json
{
  "success": true,
  "count": 7,
  "data": [...]
}
```

### ✅ Get Book by ID
```bash
curl http://localhost:3000/books/1 | jq
```

**Expected Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "1",
    "title": "The Pragmatic Programmer",
    "author": "Andy Hunt and Dave Thomas",
    "isbn": "978-0135957059",
    "publishedYear": 1999,
    "available": true
  }
}
```

### ❌ Get Non-Existent Book
```bash
curl http://localhost:3000/books/999 | jq
```

**Expected Response (404):**
```json
{
  "error": "Not Found",
  "message": "Book with ID 999 not found"
}
```

---

## Books - Create Operations

### ✅ Create Book (Complete Data)
```bash
curl -X POST http://localhost:3000/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Clean Architecture",
    "author": "Robert C. Martin",
    "isbn": "978-0134494166",
    "publishedYear": 2017,
    "available": true
  }' | jq
```

**Expected Response (201):**
```json
{
  "success": true,
  "message": "Book created successfully",
  "data": {
    "id": "8",
    "title": "Clean Architecture",
    "author": "Robert C. Martin",
    "isbn": "978-0134494166",
    "publishedYear": 2017,
    "available": true
  }
}
```

### ✅ Create Book (Minimal Data)
```bash
curl -X POST http://localhost:3000/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Minimal Book",
    "author": "Test Author",
    "isbn": "9781234567890"
  }' | jq
```

### ❌ Create Book - Missing Required Fields
```bash
curl -X POST http://localhost:3000/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Incomplete Book"
  }' | jq
```

**Expected Response (400):**
```json
{
  "error": "Bad Request",
  "message": "Missing required fields: title, author, and isbn are required",
  "received": {
    "title": true,
    "author": false,
    "isbn": false
  }
}
```

### ❌ Create Book - Invalid ISBN
```bash
curl -X POST http://localhost:3000/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Bad ISBN Book",
    "author": "Test Author",
    "isbn": "invalid-isbn"
  }' | jq
```

**Expected Response (400):**
```json
{
  "error": "Bad Request",
  "message": "Invalid ISBN format. ISBN should be 10 or 13 digits (hyphens and spaces allowed)",
  "example": "978-0135957059"
}
```

### ❌ Create Book - Invalid Year
```bash
curl -X POST http://localhost:3000/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Future Book",
    "author": "Test Author",
    "isbn": "9781234567890",
    "publishedYear": 3000
  }' | jq
```

### ❌ Create Book - No Authentication
```bash
curl -X POST http://localhost:3000/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Unauthorized Book",
    "author": "Test Author",
    "isbn": "9781234567890"
  }' | jq
```

**Expected Response (401):**
```json
{
  "error": "Access denied. No token provided.",
  "message": "Authorization header with Bearer token is required"
}
```

---

## Books - Update Operations

### ✅ Update Book Availability
```bash
curl -X PUT http://localhost:3000/books/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "available": false
  }' | jq
```

**Expected Response (200):**
```json
{
  "success": true,
  "message": "Book updated successfully",
  "data": {
    "id": "1",
    "title": "The Pragmatic Programmer",
    "author": "Andy Hunt and Dave Thomas",
    "isbn": "978-0135957059",
    "publishedYear": 1999,
    "available": false
  }
}
```

### ✅ Update Multiple Fields
```bash
curl -X PUT http://localhost:3000/books/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Updated Title",
    "publishedYear": 2020,
    "available": false
  }' | jq
```

### ❌ Update Non-Existent Book
```bash
curl -X PUT http://localhost:3000/books/999 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Updated"
  }' | jq
```

**Expected Response (404):**
```json
{
  "error": "Not Found",
  "message": "Book with ID 999 not found"
}
```

---

## Books - Delete Operations

### ✅ Delete Book
```bash
curl -X DELETE http://localhost:3000/books/3 \
  -H "Authorization: Bearer $TOKEN" | jq
```

**Expected Response (200):**
```json
{
  "success": true,
  "message": "Book deleted successfully",
  "deletedId": "3"
}
```

### ❌ Delete Non-Existent Book
```bash
curl -X DELETE http://localhost:3000/books/999 \
  -H "Authorization: Bearer $TOKEN" | jq
```

**Expected Response (404):**
```json
{
  "error": "Not Found",
  "message": "Book with ID 999 not found"
}
```

### ❌ Delete Without Authentication
```bash
curl -X DELETE http://localhost:3000/books/1 | jq
```

**Expected Response (401):**
```json
{
  "error": "Access denied. No token provided.",
  "message": "Authorization header with Bearer token is required"
}
```

---

## Error Scenarios

### Invalid Endpoint
```bash
curl http://localhost:3000/invalid | jq
```

**Expected Response (404):**
```json
{
  "error": "Not Found",
  "message": "Cannot GET /invalid",
  "availableEndpoints": [...]
}
```

### Invalid Method
```bash
curl -X PATCH http://localhost:3000/books/1 \
  -H "Authorization: Bearer $TOKEN" | jq
```

### Invalid Token
```bash
curl -X POST http://localhost:3000/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid-token" \
  -d '{
    "title": "Test",
    "author": "Test",
    "isbn": "9781234567890"
  }' | jq
```

**Expected Response (401):**
```json
{
  "error": "Invalid or expired token",
  "message": "jwt malformed"
}
```

---

## Test Data

### Valid ISBNs
- `978-0135957059` (ISBN-13 with hyphens)
- `9780135957059` (ISBN-13 without hyphens)
- `0135957052` (ISBN-10)
- `978 0135957059` (ISBN-13 with spaces)

### Invalid ISBNs
- `invalid-isbn` (not numeric)
- `123` (too short)
- `12345678901234` (too long)

### Valid Book Objects
```json
{
  "title": "Extreme Programming Explained",
  "author": "Kent Beck",
  "isbn": "978-0321278654",
  "publishedYear": 2004,
  "available": true
}
```

```json
{
  "title": "Domain-Driven Design",
  "author": "Eric Evans",
  "isbn": "978-0321125217",
  "publishedYear": 2003,
  "available": false
}
```

```json
{
  "title": "Working Effectively with Legacy Code",
  "author": "Michael Feathers",
  "isbn": "978-0131177055",
  "publishedYear": 2004,
  "available": true
}
```

---

## Complete Test Flow

```bash
#!/bin/bash
# Complete API test flow

BASE_URL="http://localhost:3000"

echo "1. Health Check"
curl -s $BASE_URL/health | jq '.status'

echo -e "\n2. Get All Books (Before)"
INITIAL_COUNT=$(curl -s $BASE_URL/books | jq '.count')
echo "Initial count: $INITIAL_COUNT"

echo -e "\n3. Login"
TOKEN=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"test123"}' \
  | jq -r '.token')
echo "Token received: ${TOKEN:0:20}..."

echo -e "\n4. Create Book"
NEW_BOOK=$(curl -s -X POST $BASE_URL/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Test Automation Book",
    "author": "SDET Team",
    "isbn": "978-9999999999",
    "publishedYear": 2024
  }')
NEW_ID=$(echo $NEW_BOOK | jq -r '.data.id')
echo "Created book ID: $NEW_ID"

echo -e "\n5. Get Created Book"
curl -s $BASE_URL/books/$NEW_ID | jq '.data.title'

echo -e "\n6. Update Book"
curl -s -X PUT $BASE_URL/books/$NEW_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"available": false}' | jq '.data.available'

echo -e "\n7. Delete Book"
curl -s -X DELETE $BASE_URL/books/$NEW_ID \
  -H "Authorization: Bearer $TOKEN" | jq '.message'

echo -e "\n8. Verify Deletion"
curl -s $BASE_URL/books/$NEW_ID | jq '.error'

echo -e "\n9. Get All Books (After)"
FINAL_COUNT=$(curl -s $BASE_URL/books | jq '.count')
echo "Final count: $FINAL_COUNT"

echo -e "\nTest Complete!"
```

---

## Python Examples

```python
import requests

BASE_URL = "http://localhost:3000"

# Login
response = requests.post(
    f"{BASE_URL}/auth/login",
    json={"username": "admin", "password": "test123"}
)
token = response.json()["token"]

# Get all books
response = requests.get(f"{BASE_URL}/books")
print(f"Total books: {response.json()['count']}")

# Create book
headers = {"Authorization": f"Bearer {token}"}
new_book = {
    "title": "Python Testing Book",
    "author": "Test Author",
    "isbn": "978-1234567890",
    "publishedYear": 2024
}
response = requests.post(
    f"{BASE_URL}/books",
    json=new_book,
    headers=headers
)
print(f"Created: {response.json()}")
```

---

## JavaScript Examples

```javascript
const BASE_URL = 'http://localhost:3000';

// Login
const login = async () => {
  const response = await fetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      username: 'admin',
      password: 'test123'
    })
  });
  const data = await response.json();
  return data.token;
};

// Get all books
const getAllBooks = async () => {
  const response = await fetch(`${BASE_URL}/books`);
  return response.json();
};

// Create book
const createBook = async (token) => {
  const response = await fetch(`${BASE_URL}/books`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      title: 'JavaScript Testing',
      author: 'Test Author',
      isbn: '978-1234567890',
      publishedYear: 2024
    })
  });
  return response.json();
};

// Usage
(async () => {
  const token = await login();
  const books = await getAllBooks();
  console.log(`Total books: ${books.count}`);
  
  const newBook = await createBook(token);
  console.log('Created:', newBook);
})();
```

---

**Happy Testing!** 🚀

