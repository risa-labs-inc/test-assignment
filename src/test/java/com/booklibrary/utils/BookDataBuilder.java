package com.booklibrary.utils;

import com.booklibrary.models.Book;
import com.github.javafaker.Faker;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Builder class to generate test data for Book objects
 */
public class BookDataBuilder {
    
    private static final Faker faker = new Faker();
    
    private String id;
    private String title;
    private String author;
    private String isbn;
    private Integer publishedYear;
    private Boolean available;
    
    public BookDataBuilder() {
        // Default values using Faker
        this.title = faker.book().title();
        this.author = faker.book().author();
        this.isbn = generateValidISBN13();
        this.publishedYear = ThreadLocalRandom.current().nextInt(1900, 2025);
        this.available = true;
    }
    
    public static BookDataBuilder aBook() {
        return new BookDataBuilder();
    }
    
    public static BookDataBuilder aValidBook() {
        return new BookDataBuilder()
                .withTitle("Test Driven Development")
                .withAuthor("Kent Beck")
                .withIsbn("978-0321146533")
                .withPublishedYear(2002)
                .withAvailable(true);
    }
    
    public BookDataBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    public BookDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public BookDataBuilder withAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public BookDataBuilder withIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }
    
    public BookDataBuilder withPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
        return this;
    }
    
    public BookDataBuilder withAvailable(Boolean available) {
        this.available = available;
        return this;
    }
    
    public Book build() {
        return Book.builder()
                .id(id)
                .title(title)
                .author(author)
                .isbn(isbn)
                .publishedYear(publishedYear)
                .available(available)
                .build();
    }
    
    /**
     * Generates a valid ISBN-13 format
     */
    private String generateValidISBN13() {
        long randomNum = ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L);
        return "978" + randomNum;
    }
    
    /**
     * Generates an invalid ISBN
     */
    public static String getInvalidISBN() {
        return "invalid-isbn-123";
    }
}

