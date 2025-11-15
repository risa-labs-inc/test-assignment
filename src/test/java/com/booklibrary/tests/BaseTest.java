package com.booklibrary.tests;

import com.booklibrary.api.AuthAPI;
import com.booklibrary.api.BooksAPI;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base test class providing common setup and utilities for all tests
 */
public class BaseTest {
    
    protected AuthAPI authAPI;
    protected BooksAPI booksAPI;
    protected String authToken;
    
    @BeforeClass(alwaysRun = true)
    public void setupApis() {
        authAPI = new AuthAPI();
        booksAPI = new BooksAPI();
        System.out.println("🔧 API clients initialized");
    }
    
    @BeforeMethod(alwaysRun = true)
    public void getAuthToken() {
        authToken = authAPI.loginWithDefaultCredentials();
        System.out.println("🔑 Auth token obtained: " + authToken.substring(0, 20) + "...");
    }
}

