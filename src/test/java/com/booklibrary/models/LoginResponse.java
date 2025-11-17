package com.booklibrary.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private String expiresIn;
    private User user;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String username;
        private String role;
    }
}

