// In-memory data store for books
let books = [
  {
    id: "1",
    title: "The Pragmatic Programmer",
    author: "Andy Hunt and Dave Thomas",
    isbn: "978-0135957059",
    publishedYear: 1999,
    available: true
  },
  {
    id: "2",
    title: "Clean Code",
    author: "Robert C. Martin",
    isbn: "978-0132350884",
    publishedYear: 2008,
    available: true
  },
  {
    id: "3",
    title: "Design Patterns",
    author: "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides",
    isbn: "978-0201633610",
    publishedYear: 1994,
    available: false
  },
  {
    id: "4",
    title: "Refactoring",
    author: "Martin Fowler",
    isbn: "978-0134757599",
    publishedYear: 2018,
    available: true
  },
  {
    id: "5",
    title: "Test Driven Development",
    author: "Kent Beck",
    isbn: "978-0321146530",
    publishedYear: 2002,
    available: true
  },
  {
    id: "6",
    title: "The Art of Software Testing",
    author: "Glenford J. Myers",
    isbn: "978-1118031964",
    publishedYear: 2011,
    available: true
  },
  {
    id: "7",
    title: "Continuous Delivery",
    author: "Jez Humble and David Farley",
    isbn: "978-0321601919",
    publishedYear: 2010,
    available: false
  }
];

let nextId = 8;

// Helper functions for data operations
const getAllBooks = () => {
  return [...books];
};

const getBookById = (id) => {
  return books.find(book => book.id === id);
};

const createBook = (bookData) => {
  const newBook = {
    id: String(nextId++),
    title: bookData.title,
    author: bookData.author,
    isbn: bookData.isbn,
    publishedYear: bookData.publishedYear || new Date().getFullYear(),
    available: bookData.available !== undefined ? bookData.available : true
  };
  books.push(newBook);
  return newBook;
};

const updateBook = (id, bookData) => {
  const index = books.findIndex(book => book.id === id);
  if (index === -1) return null;
  
  books[index] = {
    ...books[index],
    ...bookData,
    id: books[index].id // Ensure ID cannot be changed
  };
  return books[index];
};

const deleteBook = (id) => {
  const index = books.findIndex(book => book.id === id);
  if (index === -1) return false;
  
  books.splice(index, 1);
  return true;
};

// ISBN validation helper
const isValidISBN = (isbn) => {
  if (!isbn) return false;
  // Basic validation for ISBN-10 or ISBN-13 format
  const cleanISBN = isbn.replace(/[-\s]/g, '');
  return /^(?:\d{10}|\d{13})$/.test(cleanISBN);
};

module.exports = {
  getAllBooks,
  getBookById,
  createBook,
  updateBook,
  deleteBook,
  isValidISBN
};

