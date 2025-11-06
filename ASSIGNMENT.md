# Senior SDET - Take Home Assignment

## Overview

**AI Usage:** Allowed and encouraged (ChatGPT, Copilot, etc.)  
**Submission:** GitHub repository link

---

## Context

You're joining a team that's building a **Book Library API**. The API is partially built, but there's no test automation yet. Your task is to demonstrate how you'd approach testing this API and set up a foundation for the team.

---

## The API (Mock Service Provided)

We've provided a **mock API** that you'll run locally using Docker.

### Available Endpoints:

```
GET    /books                 # List all books
GET    /books/:id             # Get book by ID
POST   /books                 # Create a new book
PUT    /books/:id             # Update a book
DELETE /books/:id             # Delete a book
POST   /auth/login            # Login (returns JWT token)
```

### Sample Book Object:
```json
{
  "id": "123",
  "title": "The Pragmatic Programmer",
  "author": "Andy Hunt",
  "isbn": "978-0135957059",
  "publishedYear": 1999,
  "available": true
}
```

### Authentication:
- Most endpoints require a Bearer token
- Login with: `POST /auth/login` with `{ "username": "admin", "password": "test123" }`

---

## Your Task (Choose Your Own Adventure)

Pick **ONE** of the following paths based on your strength:

---

## Path A: API Test Automation (Recommended for most candidates)

### Requirements:

**Part 1: Core Test Suite**

Build a small but production-ready API test suite that covers:

1. **Authentication Flow**
   - Valid login returns a token
   - Invalid credentials return 401
   - Protected endpoints reject requests without tokens

2. **CRUD Operations** (pick at least 2)
   - Create a book successfully
   - Retrieve a book by ID
   - Update a book
   - Delete a book
   - Handle non-existent book IDs (404)

3. **Data Validation** (pick at least 1)
   - Validate response schema (e.g., book object has required fields)
   - Test with invalid data (missing required fields, invalid ISBN format)

**Part 2: Documentation**

Write a brief `README.md` that includes:
- How to run your tests
- Your design decisions (why this framework, why this structure)
- What you'd improve with more time

---

### What We're Looking For:

✅ **Must Have:**
- Tests run successfully and pass
- Clean, readable code structure
- At least 5 meaningful test cases
- Basic error handling
- Clear README with setup instructions

✅ **Nice to Have:**
- Reusable helper functions or base classes
- Test data management strategy
- Environment configuration (dev/staging URLs)
- Parallel execution capability
- CI/CD integration (GitHub Actions example)

✅ **Bonus Points:**
- Schema validation (JSON Schema, Zod, etc.)
- Retry logic for flaky network calls
- Thoughtful assertions with good error messages
- Test reporting (HTML report, allure, etc.)

---

### Technology Choices (Pick ONE):

**Option 1: JavaScript/TypeScript**
- Framework: Jest + Supertest, or Playwright Test API, or Mocha/Chai + Axios
- Your choice of any npm packages

**Option 2: Python**
- Framework: pytest + requests, or robot framework
- Your choice of any pip packages

**Option 3: Java**
- Framework: RestAssured + JUnit/TestNG
- Your choice of any Maven/Gradle dependencies

**We don't care which you pick—use what you're fastest with.**

---

## Path B: Test Strategy & Framework Design (For senior architects)

If you're more strategic than hands-on coding, choose this path:

### Requirements:

**Part 1: Framework Design Document**

Create a detailed design document (`DESIGN.md`) for a production-grade API test automation framework for this book library. Include:

1. **Architecture Diagram**
   - Layers (e.g., test layer, API client layer, data layer)
   - How components interact

2. **Technology Stack**
   - Framework choice with justification
   - Supporting libraries (assertion, reporting, data generation)
   - CI/CD integration plan

3. **Test Data Strategy**
   - How to create/manage test data
   - Handling test isolation
   - Cleanup strategy

4. **Reporting & Observability**
   - What metrics you'd track
   - How failures are reported
   - Integration with monitoring tools

5. **Scalability Plan**
   - How to run 500+ tests in under 10 minutes
   - Parallel execution strategy
   - Environment management

**Part 2: Sample Code**

Write **one** well-documented example test that demonstrates:
- Your proposed framework structure
- A reusable API client pattern
- Schema validation approach

---

## Path C: Performance & Chaos Testing (For specialists)

### Requirements:

**Part 1: Performance Test Suite**

Using **k6**, **Artillery**, or **JMeter**, create:

1. **Load Test Scenario**
   - Simulate 100 concurrent users for 2 minutes
   - Mix of read (70%) and write (30%) operations
   - Define pass/fail thresholds (e.g., p95 response time < 500ms, error rate < 1%)

2. **Spike Test Scenario**
   - Sudden spike from 10 to 500 users
   - Observe how the system behaves

3. **Chaos Experiment** (choose one)
   - What happens if the auth service is slow (add 2s delay)?
   - What happens if 10% of requests to DELETE fail?

**Part 2: Analysis Report**

Write a brief report (`RESULTS.md`) with:
- Graphs/screenshots of your test results
- Identified bottlenecks or failure points
- Recommendations for the dev team

---

## Submission Guidelines

### What to Submit:

1. **GitHub Repository** (public or private with access granted)
   - All code and documentation
   - Clear folder structure
   - `.gitignore` (no node_modules, etc.)

2. **README.md must include:**
   ```markdown
   # Book Library API Tests
   
   ## Setup
   [How to install dependencies and run]
   
   ## Running Tests
   [Exact commands to execute]
   
   ## Design Decisions
   [Why you chose this approach]
   
   ## What's Next
   [What you'd add with more time]
   ```

### How We'll Evaluate:

| Criteria | Weight | What We Look For |
|----------|--------|------------------|
| **Does it work?** | 30% | Tests run successfully; clear setup instructions |
| **Code Quality** | 25% | Readable, maintainable, follows best practices |
| **Test Coverage** | 20% | Meaningful test cases; edge cases considered |
| **Strategic Thinking** | 15% | Good design decisions; scalability considered |
| **Communication** | 10% | Clear documentation and code readability |

---

## Ground Rules

### ✅ You CAN:
- Use AI tools (ChatGPT, GitHub Copilot, etc.)
- Use any open-source libraries
- Search documentation online
- Use starter templates or boilerplates

### ❌ You CANNOT:
- Get help from another person
- Submit work you've done previously (must be original)

**We value quality over quantity. 5 excellent tests > 15 mediocre tests.**

---

## Mock API Setup

### Run with Docker

```bash
# Navigate to the API directory
cd book-library-api-mock

# Start the server
docker-compose up

# API runs on http://localhost:3000
```

**Prerequisites:** Docker Desktop must be installed and running.

See `SETUP_GUIDE.md` for detailed Docker setup instructions and troubleshooting.

---

## Frequently Asked Questions

**Q: What if the API is down or buggy?**  
A: Document what you encountered. We're also evaluating how you handle unexpected issues. You can mock the API responses if needed.

**Q: Can I use a framework I've built before?**  
A: Yes, but you must write NEW tests for this assignment. Don't just copy-paste old work.

**Q: Can I ask clarifying questions?**  
A: Yes, feel free to email us. Make reasonable assumptions and document them in your README.

**Q: Do I need to make tests pass on the first try?**  
A: No. We understand you might encounter issues. Document any challenges you encountered in your README.

**Q: What if I'm stronger in strategy than coding?**  
A: Choose Path B (design document). We value strategic thinking highly.

---

## Evaluation Rubric (What We'll Score)

### Strong Submission (Hire Recommendation):
- ✅ Tests run without errors
- ✅ Clean code structure (helpers, abstractions)
- ✅ Handles happy path + at least 2 error scenarios
- ✅ Good documentation
- ✅ Demonstrates senior-level thinking (reusability, maintainability)

### Borderline Submission:
- ✅ Tests run but with some manual setup needed
- ⚠️ Code works but lacks structure
- ⚠️ Only happy path tests
- ⚠️ Minimal documentation

### Weak Submission (No Hire):
- ❌ Tests don't run or have major errors
- ❌ Copy-pasted code without understanding
- ❌ No clear structure or design
- ❌ Poor or missing documentation

---

## After Submission

**Timeline:**
- We'll review your submission and provide feedback
- You'll get feedback regardless of outcome

**What Happens Next:**
- Strong submissions → Schedule final round interview
- Borderline → We might ask clarifying questions via email
- We'll provide constructive feedback even if we don't move forward

---

## Example Project Structure (Reference)

Here's what a good submission might look like:

```
book-library-tests/
├── README.md                 # Setup and instructions
├── package.json              # Dependencies
├── .env.example              # Environment variables template
├── tests/
│   ├── auth.test.js          # Authentication tests
│   ├── books.test.js         # CRUD tests
│   └── validation.test.js    # Schema/validation tests
├── utils/
│   ├── apiClient.js          # Reusable HTTP client
│   ├── testData.js           # Test data generators
│   └── schemas.js            # JSON schemas for validation
├── config/
│   └── env.js                # Environment configuration
└── .github/
    └── workflows/
        └── tests.yml         # CI example (bonus)
```

---

## Need Help?

**Technical Issues:** support@company.com  
**Questions:** hiring@company.com

---

## Final Tips for Success

1. **Start simple:** Get 1 test working end-to-end before adding more
2. **Focus on structure:** We care more about how you organize code than how many tests you write
3. **Think like a senior:** Consider maintainability, not just functionality
4. **Use AI wisely:** It's a tool, not a replacement for thinking. Review and understand generated code.
5. **Communicate clearly:** Your documentation and code comments matter as much as the code itself

---

**Good luck! We're excited to see your approach. Remember: we're evaluating your thought process and decision-making, not just your output. Quality > Quantity.**

---

## Submission Checklist

Before you submit, verify:

- [ ] Tests run successfully with clear instructions
- [ ] README.md is complete with setup steps
- [ ] Code is commented where necessary
- [ ] At least 3-5 meaningful test cases
- [ ] Repository is accessible (public or collaborator added)
- [ ] No sensitive data committed (tokens, passwords)

---

**Good luck!** 🚀