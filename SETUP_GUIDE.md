# 📚 Book Library API - Setup Guide

This guide will help you set up and test the Book Library API mock server using Docker.

---

## 🐳 Docker Setup

### Prerequisites
- Docker Desktop installed and running
- Docker Compose (usually included with Docker Desktop)

### Step 1: Start Docker Desktop

1. **Open Docker Desktop** application
2. **Wait for it to start** - Look for Docker icon in menu bar (whale icon)
3. **Verify it's running:**
   ```bash
   docker ps
   ```
   Should return empty list or running containers (not an error)

### Step 2: Start the API

```bash
# Navigate to the API directory
cd book-library-api-mock

# Start the server
docker-compose up
```

The API will start on `http://localhost:3000`

**Note:** The first time you run this, Docker will build the image which may take a minute or two.

### Step 3: Verify It's Running

In a new terminal (keep the first one running):

```bash
# Health check
curl http://localhost:3000/health

# Expected response:
# {"status":"healthy","timestamp":"...","uptime":...,"environment":"development"}
```

### Step 4: Test Basic Endpoints

```bash
# Get all books
curl http://localhost:3000/books

# Login and get token
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"test123"}'
```

### Stopping the Server

Press `Ctrl+C` in the terminal where Docker is running, or:

```bash
docker-compose down
```

---

## 🐳 Docker Troubleshooting

### Issue: Docker daemon not running

**Symptoms:** `Cannot connect to the Docker daemon`

**Solution:**
1. Open Docker Desktop application
2. Wait 30-60 seconds for it to fully start
3. Check menu bar for Docker icon (should be running, not stopped)
4. Verify: `docker ps` should not show an error

### Issue: Port 3000 already in use

**Symptoms:** `Bind for 0.0.0.0:3000 failed: port is already allocated`

**Solution:**
```bash
# Check what's using the port
lsof -i :3000

# Stop the other service, or modify docker-compose.yml:
# Change "3000:3000" to "3001:3000" in ports section
# Then access API at http://localhost:3001
```

### Issue: Container won't start

**Solution:**
```bash
# Stop and remove containers
docker-compose down

# Clean rebuild
docker-compose up --build

# If still having issues, check logs
docker-compose logs
```

### Issue: Changes not reflecting

**Solution:**
```bash
# Rebuild the image
docker-compose up --build
```

---

## 📮 Postman Setup

### Option 1: Import Collection (Easiest)

1. **Open Postman** (or Insomnia)

2. **Import Collection:**
   - Click "Import" button
   - Select `POSTMAN_COLLECTION.json` file from `book-library-api-mock/` directory
   - Collection will appear in your sidebar

3. **Set Environment Variables:**
   - Create a new environment (or use default)
   - Add variable: `baseUrl` = `http://localhost:3000`
   - Add variable: `authToken` = (leave empty, will be auto-filled)

4. **Test the Collection:**
   - Run "Login - Valid" request first
   - Token will be automatically saved to `authToken` variable
   - All other requests will use this token automatically

### Option 2: Manual Setup

1. **Create New Collection:**
   - Name: "Book Library API"

2. **Set Collection Variables:**
   - `baseUrl`: `http://localhost:3000`
   - `authToken`: (empty initially)

3. **Add Requests:**

   **Authentication:**
   - `POST {{baseUrl}}/auth/login`
   - Body (JSON):
     ```json
     {
       "username": "admin",
       "password": "test123"
     }
     ```
   - Tests tab (to auto-save token):
     ```javascript
     if (pm.response.code === 200) {
         var jsonData = pm.response.json();
         pm.collectionVariables.set("authToken", jsonData.token);
     }
     ```

   **Books:**
   - `GET {{baseUrl}}/books`
   - `GET {{baseUrl}}/books/1`
   - `POST {{baseUrl}}/books` (requires `Authorization: Bearer {{authToken}}`)
   - `PUT {{baseUrl}}/books/1` (requires `Authorization: Bearer {{authToken}}`)
   - `DELETE {{baseUrl}}/books/1` (requires `Authorization: Bearer {{authToken}}`)

4. **Set Authorization:**
   - For protected endpoints, add header:
     - Key: `Authorization`
     - Value: `Bearer {{authToken}}`

### Quick Test in Postman

1. Run "Login - Valid" request
2. Check that `authToken` variable is set (View → Show Postman Console)
3. Run "Get All Books" - should return 7 books
4. Run "Create Book - Valid" - should create a new book
5. Run "Get Book by ID" with the new book's ID

---

## 🧪 Running Automated Tests

The API includes an automated test suite:

```bash
# Make sure API is running in Docker first
cd book-library-api-mock

# Run tests
node test-api.js
```

**Expected Output:**
```
✅ PASS: Health Check
✅ PASS: Get All Books
✅ PASS: Get Book by ID
... (15 tests total)

🎉 All tests passed!
```

---

## 📋 Quick Reference

### Base URL
- `http://localhost:3000`

### Authentication
- **Endpoint:** `POST /auth/login`
- **Credentials:** `admin` / `test123`
- **Returns:** JWT token (valid 24 hours)

### Endpoints
- `GET /health` - Health check (public)
- `GET /books` - List all books (public)
- `GET /books/:id` - Get book by ID (public)
- `POST /books` - Create book (requires auth)
- `PUT /books/:id` - Update book (requires auth)
- `DELETE /books/:id` - Delete book (requires auth)

### Sample Book Object
```json
{
  "id": "1",
  "title": "The Pragmatic Programmer",
  "author": "Andy Hunt and Dave Thomas",
  "isbn": "978-0135957059",
  "publishedYear": 1999,
  "available": true
}
```

---

## 🆘 Common Issues

### API won't start
- Make sure Docker Desktop is running
- Check if port 3000 is available: `lsof -i :3000`
- Try rebuilding: `docker-compose up --build`

### Can't connect to API
- Verify server is running: `curl http://localhost:3000/health`
- Check Docker container status: `docker ps`
- Check logs: `docker-compose logs`

### Authentication fails
- Make sure you're using correct credentials: `admin` / `test123`
- Check token format: `Authorization: Bearer <token>`
- Tokens expire after 24 hours - get a new one

### Data resets
- This is expected! Data is stored in-memory
- Restarting the container resets all data
- Initial 7 books are restored on restart

---

## ✅ Verification Checklist

Before starting your assignment, verify:

- [ ] Docker Desktop is installed and running
- [ ] `docker ps` command works without errors
- [ ] API starts successfully: `docker-compose up`
- [ ] Health check returns `{"status":"healthy"}`
- [ ] Can get all books: `GET /books` returns 7 books
- [ ] Can login: `POST /auth/login` returns a token
- [ ] Can create book: `POST /books` with token works
- [ ] Postman collection imports successfully (optional)

---

**Ready to start testing!** 🚀

For API examples and test scenarios, see `API_EXAMPLES.md`
