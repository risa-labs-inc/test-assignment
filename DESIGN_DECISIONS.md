# Design Document - Book Library API Test Framework

## Architecture Overview

This document outlines the design decisions and architecture of the Book Library API test automation framework.

## 1. Architecture Pattern

### Three-Layer Architecture

```
┌─────────────────────────────────────────┐
│         Test Layer                      │
│  (AuthenticationTests, BooksCRUDTests)  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         API Client Layer                │
│     (AuthAPI, BooksAPI, BaseAPI)        │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         HTTP Client (RestAssured)       │
│         Actual API Endpoints            │
└─────────────────────────────────────────┘
```

### Benefits:
1. **Separation of Concerns**: Each layer has a single responsibility
2. **Maintainability**: Changes to API structure only affect the API Client layer
3. **Reusability**: API clients can be reused across different test scenarios
4. **Testability**: Easy to mock layers for unit testing

## 2. Key Design Patterns

### 2.1 Builder Pattern
**Used in**: Test data creation (BookDataBuilder, LoginDataBuilder)

```java
Book book = BookDataBuilder.aValidBook()
    .withTitle("Custom Title")
    .withAuthor("Custom Author")
    .build();
```

**Advantages:**
- Fluent API for test data creation
- Flexible and readable
- Easy to create variations

### 2.2 Page Object Model (Adapted for API)
**Used in**: API Client classes (AuthAPI, BooksAPI)

```java
public class BooksAPI extends BaseAPI {
    public Response getAllBooks() { ... }
    public Response getBookById(String id) { ... }
}
```

**Advantages:**
- Encapsulates API endpoints
- Central location for API operations
- Easy to maintain

### 2.3 Factory Pattern
**Used in**: ConfigManager

```java
private static final TestConfig CONFIG = ConfigFactory.create(TestConfig.class);
```

**Advantages:**
- Single source of configuration
- Environment-specific settings
- Easy to extend

## 3. Framework Components

### 3.1 API Client Layer

**BaseAPI.java**
- Provides common functionality for all API clients
- Manages RequestSpecification
- Handles authentication headers

**AuthAPI.java**
- Handles login operations
- Token management

**BooksAPI.java**
- All CRUD operations for books
- Both authenticated and non-authenticated methods

### 3.2 Configuration Management

**TestConfig.java**
- Interface defining all configuration properties
- Supports multiple sources (system properties, files)

**ConfigManager.java**
- Singleton access to configuration
- Thread-safe configuration access

### 3.3 Test Data Management

**BookDataBuilder.java**
- Builder for Book objects
- Uses JavaFaker for random data
- Provides valid and invalid data presets

**LoginDataBuilder.java**
- Builder for Login requests
- Provides valid and invalid credentials

### 3.4 Models (POJOs)

All models use:
- **Lombok** annotations (@Data, @Builder) for reduced boilerplate
- **Jackson** annotations for JSON mapping
- **Immutability** where appropriate

### 3.5 Utility Classes

**AssertionUtils.java**
- Reusable assertion methods
- Consistent error messages
- Type-safe assertions

**RetryAnalyzer.java**
- Handles flaky test retries
- Configurable retry count

**TestListener.java**
- Logs test execution events
- Provides console feedback

## 4. Test Organization

### Test Suites

1. **testng.xml**: Full test suite with parallel execution
2. **smoke-tests.xml**: Quick smoke tests for CI
3. **regression-tests.xml**: Complete regression suite

### Test Prioritization

Tests are prioritized using TestNG's `@Test(priority = N)`:
- Priority 1: Critical happy path tests
- Priority 2-5: Secondary happy path and error scenarios
- Tests run in order within each test class

## 5. Error Handling Strategy

### Positive Tests
- Assert expected status codes
- Validate response structure
- Verify data integrity

### Negative Tests
- Invalid authentication
- Missing required fields
- Invalid data formats
- Non-existent resources
- Unauthorized access attempts

## 6. Reporting Strategy

### Multi-Level Reporting

1. **Console Output**
   - Emoji-based status indicators
   - Real-time feedback
   - Summary statistics

2. **TestNG HTML Reports**
   - Built-in TestNG reports
   - Test execution summary

3. **Allure Reports**
   - Step-by-step execution details
   - Request/Response details
   - Categorization (Epic, Feature, Story)
   - Historical trends

## 7. Configuration Strategy

### Environment Configuration

```properties
# test.properties
base.url=http://localhost:3000
auth.username=admin
auth.password=test123
```

### Override Mechanism

1. File: `test.properties` (default)
2. System properties: `-Dbase.url=http://staging:3000`
3. Environment variables: `export BASE_URL=http://prod:3000`

Priority: System Properties > File > Defaults

## 8. Parallel Execution

### TestNG Parallel Configuration

```xml
<suite name="API Tests" parallel="tests" thread-count="3">
```

### Thread Safety
- Each test gets its own auth token
- No shared mutable state
- Independent test data

## 9. Schema Validation

### JSON Schema Files

Stored in `src/test/resources/schemas/`:
- `login-response-schema.json`
- `book-schema.json`
- `error-response-schema.json`

### Validation Strategy

```java
response.then().assertThat()
    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(
        "schemas/book-schema.json"));
```

**Benefits:**
- Contract validation
- Catches breaking changes
- Documents expected structure

## 10. Dependency Management

### Gradle Multi-Module Support

Framework is ready to be extended into multi-module structure:
```
root/
├── api-clients/      (Reusable API clients)
├── test-data/        (Test data management)
├── tests-functional/ (Functional tests)
└── tests-performance/(Performance tests)
```

### Dependency Versions

Centralized in `build.gradle`:
```groovy
ext {
    restAssuredVersion = '5.4.0'
    testngVersion = '7.9.0'
}
```

## 11. Extensibility

### Adding New Test Suites

1. Create new test class extending `BaseTest`
2. Use existing API clients
3. Add to TestNG XML suite
4. Run with `./gradlew test`

### Adding New Endpoints

1. Add methods to existing API client or create new one
2. Follow naming convention: `{operation}{Resource}`
3. Use `@Step` annotation for Allure

### Adding New Assertions

Add to `AssertionUtils.java`:
```java
public static void assertNewCondition(Response response) {
    // Implementation
}
```

## 12. Best Practices Implemented

1. ✅ **DRY (Don't Repeat Yourself)**
   - Reusable API clients
   - Common assertion utilities
   - Shared base classes

2. ✅ **SOLID Principles**
   - Single Responsibility: Each class has one purpose
   - Open/Closed: Easy to extend without modification
   - Dependency Inversion: Depend on abstractions

3. ✅ **Test Independence**
   - Each test can run independently
   - No test dependencies
   - Clean state for each test

4. ✅ **Clear Naming**
   - Test names describe what they test
   - Method names are self-documenting
   - Variables have meaningful names

5. ✅ **Comprehensive Assertions**
   - Multiple assertions per test
   - Clear assertion messages
   - Both positive and negative scenarios

## 13. Performance Considerations

### Test Execution Speed

- Parallel execution enabled (3 threads)
- No unnecessary waits
- Connection pooling via RestAssured
- Gradle build cache enabled

### Resource Management

- Tests clean up created resources
- No memory leaks
- Proper exception handling

## 14. Security Considerations

### Credentials Management

- Credentials in configuration files (not hardcoded)
- Support for environment variables
- .gitignore includes sensitive files

### Token Handling

- Tokens obtained fresh for each test
- No token sharing between tests
- Proper authorization testing

## 15. Maintenance Strategy

### Regular Updates

- Keep dependencies up to date
- Update schema files when API changes
- Review and refactor tests regularly

### Code Quality

- Consistent formatting
- Meaningful comments where needed
- Regular code reviews

## 16. Scalability

The framework is designed to scale:

1. **Horizontal Scaling**: Run on multiple machines
2. **Test Growth**: Easy to add new tests
3. **Data Volume**: Builder pattern supports large data sets
4. **Parallel Execution**: Configurable thread count

## Conclusion

This framework demonstrates:
- Clean architecture
- Design patterns
- Maintainability
- Scalability
- Comprehensive testing
- Excellent documentation

It's production-ready and can be extended to support:
- Multiple environments
- Different API versions
- Performance testing
- Contract testing
- CI/CD integration

