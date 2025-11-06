/**
 * Simple API Test Script
 * Run with: node test-api.js
 * 
 * Make sure the server is running before executing this script
 */

const http = require('http');

const BASE_URL = process.env.API_URL || 'http://localhost:3000';
let testResults = [];
let authToken = '';

// Helper function to make HTTP requests
function makeRequest(method, path, data = null, headers = {}) {
  return new Promise((resolve, reject) => {
    const url = new URL(path, BASE_URL);
    const options = {
      hostname: url.hostname,
      port: url.port,
      path: url.pathname,
      method: method,
      headers: {
        'Content-Type': 'application/json',
        ...headers
      }
    };

    const req = http.request(options, (res) => {
      let body = '';
      res.on('data', (chunk) => body += chunk);
      res.on('end', () => {
        try {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            body: body ? JSON.parse(body) : null
          });
        } catch (e) {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            body: body
          });
        }
      });
    });

    req.on('error', reject);

    if (data) {
      req.write(JSON.stringify(data));
    }

    req.end();
  });
}

// Test runner
async function runTest(name, testFn) {
  try {
    console.log(`\n🧪 Testing: ${name}`);
    await testFn();
    testResults.push({ name, status: 'PASS' });
    console.log(`✅ PASS: ${name}`);
  } catch (error) {
    testResults.push({ name, status: 'FAIL', error: error.message });
    console.log(`❌ FAIL: ${name}`);
    console.log(`   Error: ${error.message}`);
  }
}

// Assert helper
function assert(condition, message) {
  if (!condition) {
    throw new Error(message || 'Assertion failed');
  }
}

// Tests
async function test1_healthCheck() {
  const response = await makeRequest('GET', '/health');
  assert(response.status === 200, `Expected status 200, got ${response.status}`);
  assert(response.body.status === 'healthy', 'Health check failed');
}

async function test2_getAllBooks() {
  const response = await makeRequest('GET', '/books');
  assert(response.status === 200, `Expected status 200, got ${response.status}`);
  assert(response.body.success === true, 'Success flag not true');
  assert(Array.isArray(response.body.data), 'Data is not an array');
  assert(response.body.data.length > 0, 'No books returned');
}

async function test3_getBookById() {
  const response = await makeRequest('GET', '/books/1');
  assert(response.status === 200, `Expected status 200, got ${response.status}`);
  assert(response.body.data.id === '1', 'Book ID mismatch');
  assert(response.body.data.title, 'Book has no title');
}

async function test4_getNonExistentBook() {
  const response = await makeRequest('GET', '/books/999');
  assert(response.status === 404, `Expected status 404, got ${response.status}`);
  assert(response.body.error, 'Error message missing');
}

async function test5_loginValid() {
  const response = await makeRequest('POST', '/auth/login', {
    username: 'admin',
    password: 'test123'
  });
  assert(response.status === 200, `Expected status 200, got ${response.status}`);
  assert(response.body.token, 'No token returned');
  authToken = response.body.token;
  console.log(`   Token received: ${authToken.substring(0, 20)}...`);
}

async function test6_loginInvalid() {
  const response = await makeRequest('POST', '/auth/login', {
    username: 'wrong',
    password: 'wrong'
  });
  assert(response.status === 401, `Expected status 401, got ${response.status}`);
  assert(response.body.error, 'Error message missing');
}

async function test7_createBookWithoutAuth() {
  const response = await makeRequest('POST', '/books', {
    title: 'Test Book',
    author: 'Test Author',
    isbn: '9780000000000'
  });
  assert(response.status === 401, `Expected status 401, got ${response.status}`);
}

async function test8_createBookWithAuth() {
  const response = await makeRequest('POST', '/books', {
    title: 'API Testing Book',
    author: 'SDET Team',
    isbn: '978-1234567890',
    publishedYear: 2024,
    available: true
  }, {
    'Authorization': `Bearer ${authToken}`
  });
  assert(response.status === 201, `Expected status 201, got ${response.status}`);
  assert(response.body.data.id, 'No ID in created book');
  assert(response.body.data.title === 'API Testing Book', 'Title mismatch');
}

async function test9_createBookMissingFields() {
  const response = await makeRequest('POST', '/books', {
    title: 'Incomplete Book'
  }, {
    'Authorization': `Bearer ${authToken}`
  });
  assert(response.status === 400, `Expected status 400, got ${response.status}`);
  assert(response.body.error, 'Error message missing');
}

async function test10_createBookInvalidISBN() {
  const response = await makeRequest('POST', '/books', {
    title: 'Book with Bad ISBN',
    author: 'Author',
    isbn: 'invalid-isbn'
  }, {
    'Authorization': `Bearer ${authToken}`
  });
  assert(response.status === 400, `Expected status 400, got ${response.status}`);
  assert(response.body.message.includes('ISBN'), 'Error should mention ISBN');
}

async function test11_updateBook() {
  const response = await makeRequest('PUT', '/books/2', {
    available: false
  }, {
    'Authorization': `Bearer ${authToken}`
  });
  assert(response.status === 200, `Expected status 200, got ${response.status}`);
  assert(response.body.data.available === false, 'Book availability not updated');
}

async function test12_updateNonExistentBook() {
  const response = await makeRequest('PUT', '/books/999', {
    title: 'Updated'
  }, {
    'Authorization': `Bearer ${authToken}`
  });
  assert(response.status === 404, `Expected status 404, got ${response.status}`);
}

async function test13_deleteBook() {
  const response = await makeRequest('DELETE', '/books/3', null, {
    'Authorization': `Bearer ${authToken}`
  });
  assert(response.status === 200, `Expected status 200, got ${response.status}`);
  assert(response.body.deletedId === '3', 'Deleted ID mismatch');
}

async function test14_deleteNonExistentBook() {
  const response = await makeRequest('DELETE', '/books/999', null, {
    'Authorization': `Bearer ${authToken}`
  });
  assert(response.status === 404, `Expected status 404, got ${response.status}`);
}

async function test15_invalidToken() {
  const response = await makeRequest('POST', '/books', {
    title: 'Test',
    author: 'Test',
    isbn: '9780000000000'
  }, {
    'Authorization': 'Bearer invalid-token-here'
  });
  assert(response.status === 401, `Expected status 401, got ${response.status}`);
}

// Main execution
async function runAllTests() {
  console.log('╔════════════════════════════════════════════════════════════╗');
  console.log('║       📚 Book Library API - Test Suite                    ║');
  console.log('╚════════════════════════════════════════════════════════════╝');
  console.log(`\nTesting API at: ${BASE_URL}`);
  console.log('\nStarting tests...\n');

  // Run all tests
  await runTest('Health Check', test1_healthCheck);
  await runTest('Get All Books', test2_getAllBooks);
  await runTest('Get Book by ID', test3_getBookById);
  await runTest('Get Non-Existent Book (404)', test4_getNonExistentBook);
  await runTest('Login with Valid Credentials', test5_loginValid);
  await runTest('Login with Invalid Credentials', test6_loginInvalid);
  await runTest('Create Book Without Auth (401)', test7_createBookWithoutAuth);
  await runTest('Create Book With Auth', test8_createBookWithAuth);
  await runTest('Create Book Missing Fields (400)', test9_createBookMissingFields);
  await runTest('Create Book Invalid ISBN (400)', test10_createBookInvalidISBN);
  await runTest('Update Book', test11_updateBook);
  await runTest('Update Non-Existent Book (404)', test12_updateNonExistentBook);
  await runTest('Delete Book', test13_deleteBook);
  await runTest('Delete Non-Existent Book (404)', test14_deleteNonExistentBook);
  await runTest('Invalid Token (401)', test15_invalidToken);

  // Print summary
  console.log('\n╔════════════════════════════════════════════════════════════╗');
  console.log('║                    Test Summary                            ║');
  console.log('╚════════════════════════════════════════════════════════════╝\n');

  const passed = testResults.filter(r => r.status === 'PASS').length;
  const failed = testResults.filter(r => r.status === 'FAIL').length;

  console.log(`Total Tests: ${testResults.length}`);
  console.log(`✅ Passed: ${passed}`);
  console.log(`❌ Failed: ${failed}`);

  if (failed > 0) {
    console.log('\nFailed Tests:');
    testResults
      .filter(r => r.status === 'FAIL')
      .forEach(r => console.log(`  - ${r.name}: ${r.error}`));
  }

  console.log('\n' + (failed === 0 ? '🎉 All tests passed!' : '⚠️  Some tests failed.'));
  
  process.exit(failed > 0 ? 1 : 0);
}

// Check if server is running
async function checkServer() {
  try {
    await makeRequest('GET', '/health');
    console.log('✓ Server is running\n');
    return true;
  } catch (error) {
    console.error('❌ Cannot connect to server at', BASE_URL);
    console.error('   Make sure the server is running:');
    console.error('   npm start  (or)  docker-compose up\n');
    process.exit(1);
  }
}

// Start
(async () => {
  await checkServer();
  await runAllTests();
})();

