package com.booklibrary.tests;

import com.booklibrary.models.ApiResponse;
import com.booklibrary.models.Book;
import com.booklibrary.utils.BookDataBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static com.booklibrary.utils.AssertionUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Books Management")
@Feature("CRUD Operations")
public class BooksCRUDTests extends BaseTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test(description = "Verify getting all books returns a list", priority = 1)
    @Story("Get all books")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that GET /books returns a list of all books")
    public void testGetAllBooks() {
        // Act
        Response response = booksAPI.getAllBooks();
        
        // Assert
        assertSuccessResponse(response);
        assertResponseContainsField(response, "count");
        assertResponseContainsField(response, "data");
        
        // Verify data structure
        ApiResponse<List<Book>> apiResponse = objectMapper.convertValue(
                response.jsonPath().getMap("$"),
                new TypeReference<ApiResponse<List<Book>>>() {}
        );
        
        assertThat("Books list should not be empty", 
                   apiResponse.getData(), is(not(empty())));
        assertThat("Count should match data size", 
                   apiResponse.getCount(), equalTo(apiResponse.getData().size()));
        assertThat("Initial books count should be 7", 
                   apiResponse.getCount(), greaterThanOrEqualTo(7));
        
        // Verify first book has required fields
        Book firstBook = apiResponse.getData().get(0);
        assertThat("Book should have ID", firstBook.getId(), is(notNullValue()));
        assertThat("Book should have title", firstBook.getTitle(), is(notNullValue()));
        assertThat("Book should have author", firstBook.getAuthor(), is(notNullValue()));
        
        System.out.println("✅ Retrieved " + apiResponse.getCount() + " books");
    }
    
    @Test(description = "Verify getting a book by valid ID", priority = 2)
    @Story("Get book by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that GET /books/{id} returns the correct book")
    public void testGetBookById() {
        // Arrange
        String bookId = "1";
        
        // Act
        Response response = booksAPI.getBookById(bookId);
        
        // Assert
        assertSuccessResponse(response);
        assertResponseContainsField(response, "data");
        
        Book book = response.jsonPath().getObject("data", Book.class);
        assertThat("Book ID should match", book.getId(), equalTo(bookId));
        assertThat("Book should have title", book.getTitle(), is(notNullValue()));
        assertThat("Book should be 'The Pragmatic Programmer'", 
                   book.getTitle(), equalTo("The Pragmatic Programmer"));
        
        System.out.println("✅ Retrieved book: " + book.getTitle());
    }
    
    @Test(description = "Verify getting a non-existent book returns 404", priority = 3)
    @Story("Get book by ID - Error scenario")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that GET /books/{id} returns 404 for non-existent book")
    public void testGetNonExistentBook() {
        // Arrange
        String nonExistentId = "999";
        
        // Act
        Response response = booksAPI.getBookById(nonExistentId);
        
        // Assert
        assertErrorResponse(response, 404);
        assertErrorMessage(response, "not found");
        
        System.out.println("✅ Non-existent book correctly returns 404");
    }
    
    @Test(description = "Verify creating a book with valid data", priority = 4)
    @Story("Create book")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that POST /books creates a new book successfully")
    public void testCreateBookWithValidData() {
        // Arrange
        Book newBook = BookDataBuilder.aValidBook()
                .withTitle("Clean Code")
                .withAuthor("Robert C. Martin")
                .withIsbn("978-0132350884")
                .withPublishedYear(2008)
                .build();
        
        // Act
        Response response = booksAPI.createBook(newBook, authToken);
        
        // Assert
        assertStatusCode(response, 201);
        assertResponseFieldEquals(response, "success", true);
        assertResponseContainsField(response, "data");
        assertResponseContainsField(response, "message");
        
        Book createdBook = response.jsonPath().getObject("data", Book.class);
        assertThat("Created book should have ID", createdBook.getId(), is(notNullValue()));
        assertThat("Title should match", createdBook.getTitle(), equalTo(newBook.getTitle()));
        assertThat("Author should match", createdBook.getAuthor(), equalTo(newBook.getAuthor()));
        assertThat("ISBN should match", createdBook.getIsbn(), equalTo(newBook.getIsbn()));
        assertThat("Published year should match", 
                   createdBook.getPublishedYear(), equalTo(newBook.getPublishedYear()));
        
        System.out.println("✅ Created book with ID: " + createdBook.getId());
    }
    
    @Test(description = "Verify creating a book without authentication fails", priority = 5)
    @Story("Create book - Security scenario")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that POST /books without auth token returns 401")
    public void testCreateBookWithoutAuthentication() {
        // Arrange
        Book newBook = BookDataBuilder.aValidBook().build();
        
        // Act
        Response response = booksAPI.createBookWithoutAuth(newBook);
        
        // Assert
        assertStatusCode(response, 401);
        assertErrorResponse(response, 401);
        assertErrorMessage(response, "Authorization header with Bearer token is required");
        
        System.out.println("✅ Book creation without auth correctly rejected");
    }
    
    @Test(description = "Verify creating a book with missing required fields fails", priority = 6)
    @Story("Create book - Validation scenario")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that POST /books with missing fields returns 400")
    public void testCreateBookWithMissingFields() {
        // Arrange - Book with only title
        Book invalidBook = Book.builder()
                .title("Incomplete Book")
                .build();
        
        // Act
        Response response = booksAPI.createBook(invalidBook, authToken);
        
        // Assert
        assertStatusCode(response, 400);
        assertErrorResponse(response, 400);
        assertErrorMessage(response, "required");
        
        System.out.println("✅ Book with missing fields correctly rejected");
    }
    
    @Test(description = "Verify creating a book with invalid ISBN fails", priority = 7)
    @Story("Create book - Validation scenario")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that POST /books with invalid ISBN returns 400")
    public void testCreateBookWithInvalidISBN() {
        // Arrange
        Book invalidBook = BookDataBuilder.aValidBook()
                .withIsbn(BookDataBuilder.getInvalidISBN())
                .build();
        
        // Act
        Response response = booksAPI.createBook(invalidBook, authToken);
        
        // Assert
        assertStatusCode(response, 400);
        assertErrorResponse(response, 400);
        assertErrorMessage(response, "Invalid ISBN");
        
        System.out.println("✅ Book with invalid ISBN correctly rejected");
    }
    
    @Test(description = "Verify updating a book", priority = 8)
    @Story("Update book")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that PUT /books/{id} updates an existing book")
    public void testUpdateBook() {
        // Arrange - Create a book first
        Book newBook = BookDataBuilder.aValidBook().build();
        Response createResponse = booksAPI.createBook(newBook, authToken);
        String bookId = createResponse.jsonPath().getString("data.id");
        
        // Update data
        Book updateData = Book.builder()
                .available(false)
                .build();
        
        // Act
        Response updateResponse = booksAPI.updateBook(bookId, updateData, authToken);
        
        // Assert
        assertSuccessResponse(updateResponse);
        
        Book updatedBook = updateResponse.jsonPath().getObject("data", Book.class);
        assertThat("Book should be marked as unavailable", 
                   updatedBook.getAvailable(), equalTo(false));
//        assertThat("Other fields should remain unchanged",
//                   updatedBook.getTitle(), equalTo(newBook.getTitle()));
        
        System.out.println("✅ Updated book ID: " + bookId);
    }
    
    @Test(description = "Verify updating a non-existent book returns 404", priority = 9)
    @Story("Update book - Error scenario")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that PUT /books/{id} returns 404 for non-existent book")
    public void testUpdateNonExistentBook() {
        // Arrange
        String nonExistentId = "999";
        Book updateData = Book.builder()
                .title("Updated Title")
                .build();
        
        // Act
        Response response = booksAPI.updateBook(nonExistentId, updateData, authToken);
        
        // Assert
        assertErrorResponse(response, 404);
        assertErrorMessage(response, "not found");
        
        System.out.println("✅ Update non-existent book correctly returns 404");
    }
    
    @Test(description = "Verify deleting a book", priority = 10)
    @Story("Delete book")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that DELETE /books/{id} deletes a book successfully")
    public void testDeleteBook() {
        // Arrange - Create a book first
        Book newBook = BookDataBuilder.aValidBook().build();
        Response createResponse = booksAPI.createBook(newBook, authToken);
        String bookId = createResponse.jsonPath().getString("data.id");
        
        // Act
        Response deleteResponse = booksAPI.deleteBook(bookId, authToken);
        
        // Assert
        assertSuccessResponse(deleteResponse);
        assertResponseFieldEquals(deleteResponse, "message", "Book deleted successfully");
        assertResponseFieldEquals(deleteResponse, "deletedId", bookId);
        
        // Verify book is actually deleted
        Response getResponse = booksAPI.getBookById(bookId);
        assertStatusCode(getResponse, 404);
        
        System.out.println("✅ Deleted book ID: " + bookId);
    }
    
    @Test(description = "Verify deleting a book without authentication fails", priority = 11)
    @Story("Delete book - Security scenario")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that DELETE /books/{id} without auth token returns 401")
    public void testDeleteBookWithoutAuthentication() {
        // Act
        Response response = booksAPI.deleteBookWithoutAuth("1");
        
        // Assert
        assertStatusCode(response, 401);
        assertErrorResponse(response, 401);
        
        System.out.println("✅ Book deletion without auth correctly rejected");
    }
    
    @Test(description = "Verify deleting a non-existent book returns 404", priority = 12)
    @Story("Delete book - Error scenario")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that DELETE /books/{id} returns 404 for non-existent book")
    public void testDeleteNonExistentBook() {
        // Arrange
        String nonExistentId = "999";
        
        // Act
        Response response = booksAPI.deleteBook(nonExistentId, authToken);
        
        // Assert
        assertErrorResponse(response, 404);
        assertErrorMessage(response, "not found");
        
        System.out.println("✅ Delete non-existent book correctly returns 404");
    }
    
    @Test(description = "Verify complete CRUD workflow", priority = 13)
    @Story("Full CRUD workflow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies complete Create-Read-Update-Delete workflow")
    public void testCompleteCRUDWorkflow() {
        // 1. Create
        Book newBook = BookDataBuilder.aBook().build();
        Response createResponse = booksAPI.createBook(newBook, authToken);
        assertStatusCode(createResponse, 201);
        String bookId = createResponse.jsonPath().getString("data.id");
        System.out.println("   📝 Created book ID: " + bookId);
        
        // 2. Read
        Response readResponse = booksAPI.getBookById(bookId);
        assertSuccessResponse(readResponse);
        System.out.println("   📖 Read book: " + readResponse.jsonPath().getString("data.title"));
        
        // 3. Update
        Book updateData = Book.builder().available(false).build();
        Response updateResponse = booksAPI.updateBook(bookId, updateData, authToken);
        assertSuccessResponse(updateResponse);
        assertThat("Book should be updated", 
                   updateResponse.jsonPath().getBoolean("data.available"), equalTo(false));
        System.out.println("   ✏️ Updated book availability");
        
        // 4. Delete
        Response deleteResponse = booksAPI.deleteBook(bookId, authToken);
        assertSuccessResponse(deleteResponse);
        System.out.println("   🗑️ Deleted book ID: " + bookId);
        
        // 5. Verify deletion
        Response verifyResponse = booksAPI.getBookById(bookId);
        assertStatusCode(verifyResponse, 404);
        System.out.println("✅ Complete CRUD workflow successful");
    }
}

