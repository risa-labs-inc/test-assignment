require('dotenv').config();
const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const authRoutes = require('./routes/auth');
const bookRoutes = require('./routes/books');
const { authenticateToken } = require('./middleware/auth');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors()); // Enable CORS for all origins
app.use(express.json()); // Parse JSON request bodies
app.use(express.urlencoded({ extended: true })); // Parse URL-encoded bodies
app.use(morgan('dev')); // HTTP request logger

// Health check endpoint (public)
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: process.env.NODE_ENV || 'development'
  });
});

// Root endpoint
app.get('/', (req, res) => {
  res.status(200).json({
    message: 'Book Library API Mock Server',
    version: '1.0.0',
    endpoints: {
      auth: {
        login: 'POST /auth/login'
      },
      books: {
        getAll: 'GET /books',
        getById: 'GET /books/:id',
        create: 'POST /books (requires auth)',
        update: 'PUT /books/:id (requires auth)',
        delete: 'DELETE /books/:id (requires auth)'
      },
      health: 'GET /health'
    },
    documentation: 'See README.md for detailed API documentation'
  });
});

// Public routes
app.use('/auth', authRoutes);

// Books routes - GET endpoints are public, others require auth
// Mount the router first for GET endpoints (public)
app.use('/books', bookRoutes);

// Then protect POST, PUT, DELETE endpoints
// Note: GET endpoints are handled in the router without auth middleware
// POST, PUT, DELETE routes in books.js will need auth middleware applied there

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    error: 'Not Found',
    message: `Cannot ${req.method} ${req.path}`,
    availableEndpoints: [
      'GET /',
      'GET /health',
      'POST /auth/login',
      'GET /books',
      'GET /books/:id',
      'POST /books',
      'PUT /books/:id',
      'DELETE /books/:id'
    ]
  });
});

// Error handler
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(err.status || 500).json({
    error: err.message || 'Internal Server Error',
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`
╔════════════════════════════════════════════════════════════╗
║       📚 Book Library API Mock Server                      ║
╠════════════════════════════════════════════════════════════╣
║  Status: Running                                           ║
║  Port: ${PORT}                                              ║
║  Environment: ${process.env.NODE_ENV || 'development'}                                  ║
║  URL: http://localhost:${PORT}                              ║
╠════════════════════════════════════════════════════════════╣
║  Quick Test:                                               ║
║  curl http://localhost:${PORT}/books                        ║
╚════════════════════════════════════════════════════════════╝
  `);
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: closing HTTP server');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('\nSIGINT signal received: closing HTTP server');
  process.exit(0);
});

module.exports = app;

