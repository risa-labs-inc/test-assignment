const express = require('express');
const jwt = require('jsonwebtoken');
const { JWT_SECRET } = require('../middleware/auth');

const router = express.Router();

// Hard-coded credentials for mock API
const VALID_USERNAME = 'admin';
const VALID_PASSWORD = 'test123';

/**
 * POST /auth/login
 * Login endpoint that returns a JWT token
 * Body: { username: string, password: string }
 */
router.post('/login', (req, res) => {
  const { username, password } = req.body;

  // Validate request body
  if (!username || !password) {
    return res.status(400).json({
      error: 'Bad Request',
      message: 'Username and password are required'
    });
  }

  // Check credentials
  if (username === VALID_USERNAME && password === VALID_PASSWORD) {
    // Generate JWT token (expires in 24 hours)
    const token = jwt.sign(
      { username: username, role: 'admin' },
      JWT_SECRET,
      { expiresIn: '24h' }
    );

    return res.status(200).json({
      message: 'Login successful',
      token: token,
      expiresIn: '24h',
      user: {
        username: username,
        role: 'admin'
      }
    });
  } else {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Invalid username or password'
    });
  }
});

module.exports = router;

