package com.booklibrary.api;

import com.booklibrary.models.Book;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * API client for books endpoints
 */
public class BooksAPI extends BaseAPI {
    
    private static final String BOOKS_ENDPOINT = "/books";
    
    /**
     * Get all books
     */
    @Step("Get all books")
    public Response getAllBooks() {
        return given()
                .spec(getRequestSpec())
                .when()
                .get(BOOKS_ENDPOINT);
    }
    
    /**
     * Get book by ID
     */
    @Step("Get book by ID: {bookId}")
    public Response getBookById(String bookId) {
        return given()
                .spec(getRequestSpec())
                .pathParam("id", bookId)
                .when()
                .get(BOOKS_ENDPOINT + "/{id}");
    }
    
    /**
     * Create a new book (requires authentication)
     */
    @Step("Create book: {book.title}")
    public Response createBook(Book book, String token) {
        return given()
                .spec(getAuthenticatedRequestSpec(token))
                .body(book)
                .when()
                .post(BOOKS_ENDPOINT);
    }
    
    /**
     * Create a new book without authentication (for negative testing)
     */
    @Step("Create book without authentication: {book.title}")
    public Response createBookWithoutAuth(Book book) {
        return given()
                .spec(getRequestSpec())
                .body(book)
                .when()
                .post(BOOKS_ENDPOINT);
    }
    
    /**
     * Update a book (requires authentication)
     */
    @Step("Update book ID: {bookId}")
    public Response updateBook(String bookId, Book book, String token) {
        return given()
                .spec(getAuthenticatedRequestSpec(token))
                .pathParam("id", bookId)
                .body(book)
                .when()
                .put(BOOKS_ENDPOINT + "/{id}");
    }
    
    /**
     * Delete a book (requires authentication)
     */
    @Step("Delete book ID: {bookId}")
    public Response deleteBook(String bookId, String token) {
        return given()
                .spec(getAuthenticatedRequestSpec(token))
                .pathParam("id", bookId)
                .when()
                .delete(BOOKS_ENDPOINT + "/{id}");
    }
    
    /**
     * Delete a book without authentication (for negative testing)
     */
    @Step("Delete book without authentication, ID: {bookId}")
    public Response deleteBookWithoutAuth(String bookId) {
        return given()
                .spec(getRequestSpec())
                .pathParam("id", bookId)
                .when()
                .delete(BOOKS_ENDPOINT + "/{id}");
    }
}

