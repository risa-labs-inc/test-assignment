# Quick Start Guide

## 1. Start the API (in terminal 1)

```bash
cd book-library-api-mock
docker-compose up
```

Wait for: `Server running on port 3000`

## 2. Run Tests (in terminal 2)

### All Tests
```bash
./gradlew clean test
```

### Smoke Tests (faster)
```bash
./gradlew clean smokeTest
```

## 3. View Results

### Console
Tests will show pass/fail in terminal with ✅ and ❌

### HTML Report
```bash
open build/reports/tests/test/index.html
```

### Allure Report (Beautiful)
```bash
./gradlew allureServe
```

## 4. Test Coverage

✅ **22 comprehensive tests** covering:
- Authentication (5 tests)
- CRUD operations (13 tests)
- Schema validation (4 tests)

## 5. Expected Results

```
================================================================================
📊 Test Suite Finished: Book Library API Test Suite
   Passed: 22
   Failed: 0
   Skipped: 0
================================================================================
```

## Troubleshooting

**Tests fail with "Connection refused"**
- Make sure API is running: `curl http://localhost:3000/health`

**Port 3000 already in use**
```bash
# Find and kill process
lsof -i :3000
kill -9 <PID>
```

**Build fails**
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

For detailed documentation, see **TEST_FRAMEWORK_README.md**

