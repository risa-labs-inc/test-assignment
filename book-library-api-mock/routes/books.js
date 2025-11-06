const express = require('express');
const {
  getAllBooks,
  getBookById,
  createBook,
  updateBook,
  deleteBook,
  isValidISBN
} = require('../data/books');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

/**
 * GET /books
 * Get all books
 */
router.get('/', (req, res) => {
  try {
    const books = getAllBooks();
    res.status(200).json({
      success: true,
      count: books.length,
      data: books
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

/**
 * GET /books/:id
 * Get a single book by ID
 */
router.get('/:id', (req, res) => {
  try {
    const book = getBookById(req.params.id);
    
    if (!book) {
      return res.status(404).json({
        error: 'Not Found',
        message: `Book with ID ${req.params.id} not found`
      });
    }
    
    res.status(200).json({
      success: true,
      data: book
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

/**
 * POST /books
 * Create a new book
 * Required fields: title, author, isbn
 */
router.post('/', authenticateToken, (req, res) => {
  try {
    const { title, author, isbn, publishedYear, available } = req.body;

    // Validate required fields
    if (!title || !author || !isbn) {
      return res.status(400).json({
        error: 'Bad Request',
        message: 'Missing required fields: title, author, and isbn are required',
        received: { title: !!title, author: !!author, isbn: !!isbn }
      });
    }

    // Validate ISBN format
    if (!isValidISBN(isbn)) {
      return res.status(400).json({
        error: 'Bad Request',
        message: 'Invalid ISBN format. ISBN should be 10 or 13 digits (hyphens and spaces allowed)',
        example: '978-0135957059'
      });
    }

    // Validate publishedYear if provided
    if (publishedYear && (typeof publishedYear !== 'number' || publishedYear < 1000 || publishedYear > new Date().getFullYear() + 1)) {
      return res.status(400).json({
        error: 'Bad Request',
        message: `Invalid publishedYear. Must be a number between 1000 and ${new Date().getFullYear() + 1}`
      });
    }

    const newBook = createBook({ title, author, isbn, publishedYear, available });
    
    res.status(201).json({
      success: true,
      message: 'Book created successfully',
      data: newBook
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

/**
 * PUT /books/:id
 * Update an existing book
 */
router.put('/:id', authenticateToken, (req, res) => {
  try {
    const { title, author, isbn, publishedYear, available } = req.body;

    // Check if book exists
    const existingBook = getBookById(req.params.id);
    if (!existingBook) {
      return res.status(404).json({
        error: 'Not Found',
        message: `Book with ID ${req.params.id} not found`
      });
    }

    // Validate ISBN if provided
    if (isbn && !isValidISBN(isbn)) {
      return res.status(400).json({
        error: 'Bad Request',
        message: 'Invalid ISBN format. ISBN should be 10 or 13 digits (hyphens and spaces allowed)'
      });
    }

    // Validate publishedYear if provided
    if (publishedYear && (typeof publishedYear !== 'number' || publishedYear < 1000 || publishedYear > new Date().getFullYear() + 1)) {
      return res.status(400).json({
        error: 'Bad Request',
        message: `Invalid publishedYear. Must be a number between 1000 and ${new Date().getFullYear() + 1}`
      });
    }

    const updatedBook = updateBook(req.params.id, {
      title,
      author,
      isbn,
      publishedYear,
      available
    });

    res.status(200).json({
      success: true,
      message: 'Book updated successfully',
      data: updatedBook
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

/**
 * DELETE /books/:id
 * Delete a book
 */
router.delete('/:id', authenticateToken, (req, res) => {
  try {
    const deleted = deleteBook(req.params.id);
    
    if (!deleted) {
      return res.status(404).json({
        error: 'Not Found',
        message: `Book with ID ${req.params.id} not found`
      });
    }
    
    res.status(200).json({
      success: true,
      message: 'Book deleted successfully',
      deletedId: req.params.id
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

module.exports = router;

