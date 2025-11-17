package com.booklibrary.utils;

import com.booklibrary.models.LoginRequest;

/**
 * Builder class to generate test data for LoginRequest objects
 */
public class LoginDataBuilder {
    
    private String username;
    private String password;
    
    public LoginDataBuilder() {
        this.username = "admin";
        this.password = "test123";
    }
    
    public static LoginDataBuilder aValidLogin() {
        return new LoginDataBuilder();
    }
    
    public static LoginDataBuilder anInvalidLogin() {
        return new LoginDataBuilder()
                .withUsername("invalid")
                .withPassword("invalid");
    }
    
    public LoginDataBuilder withUsername(String username) {
        this.username = username;
        return this;
    }
    
    public LoginDataBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
    
    public LoginRequest build() {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
}

