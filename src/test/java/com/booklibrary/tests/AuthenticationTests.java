package com.booklibrary.tests;

import com.booklibrary.models.ErrorResponse;
import com.booklibrary.models.LoginRequest;
import com.booklibrary.models.LoginResponse;
import com.booklibrary.utils.LoginDataBuilder;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.booklibrary.utils.AssertionUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Authentication")
@Feature("Login Functionality")
public class AuthenticationTests extends BaseTest {
    
    @Test(description = "Verify successful login with valid credentials", priority = 1)
    @Story("Login with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that user can login with valid credentials and receive a JWT token")
    public void testLoginWithValidCredentials() {
        // Arrange
        LoginRequest loginRequest = LoginDataBuilder.aValidLogin().build();
        
        // Act
        Response response = authAPI.login(loginRequest);
        
        // Assert
        assertStatusCode(response, 200);
        assertResponseContainsField(response, "token");
        assertResponseContainsField(response, "message");
        assertResponseContainsField(response, "expiresIn");
        assertResponseContainsField(response, "user");
        
        LoginResponse loginResponse = response.as(LoginResponse.class);
        assertThat("Token should not be null or empty", 
                   loginResponse.getToken(), is(not(emptyOrNullString())));
        assertThat("Message should contain success", 
                   loginResponse.getMessage(), containsString("successful"));
        assertThat("Token expiry should be 24h", 
                   loginResponse.getExpiresIn(), equalTo("24h"));
        assertThat("Username should match", 
                   loginResponse.getUser().getUsername(), equalTo("admin"));
        assertThat("Role should match", 
                   loginResponse.getUser().getRole(), equalTo("admin"));
        
        System.out.println("✅ Successfully logged in with token: " + 
                          loginResponse.getToken().substring(0, 20) + "...");
    }
    
    @Test(description = "Verify login fails with invalid credentials", priority = 2)
    @Story("Login with invalid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies that login fails with invalid username and password")
    public void testLoginWithInvalidCredentials() {
        // Arrange
        LoginRequest loginRequest = LoginDataBuilder.anInvalidLogin().build();
        
        // Act
        Response response = authAPI.login(loginRequest);
        
        // Assert
        assertStatusCode(response, 401);
        assertErrorResponse(response, 401);
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat("Error should be Unauthorized", 
                   errorResponse.getError(), equalTo("Unauthorized"));
        assertThat("Message should mention invalid credentials", 
                   errorResponse.getMessage(), containsString("Invalid username or password"));
        
        System.out.println("✅ Invalid credentials correctly rejected");
    }
    
    @Test(description = "Verify login fails when username is missing", priority = 3)
    @Story("Login with missing fields")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that login fails when username field is missing")
    public void testLoginWithMissingUsername() {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .password("test123")
                .build();
        
        // Act
        Response response = authAPI.login(loginRequest);
        
        // Assert
        assertStatusCode(response, 400);
        assertErrorResponse(response, 400);
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat("Error should be Bad Request", 
                   errorResponse.getError(), equalTo("Bad Request"));
        assertThat("Message should mention required fields", 
                   errorResponse.getMessage(), containsString("required"));
        
        System.out.println("✅ Missing username correctly rejected");
    }
    
    @Test(description = "Verify login fails when password is missing", priority = 4)
    @Story("Login with missing fields")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that login fails when password field is missing")
    public void testLoginWithMissingPassword() {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .username("admin")
                .build();
        
        // Act
        Response response = authAPI.login(loginRequest);
        
        // Assert
        assertStatusCode(response, 400);
        assertErrorResponse(response, 400);
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat("Error should be Bad Request", 
                   errorResponse.getError(), equalTo("Bad Request"));
        assertThat("Message should mention required fields", 
                   errorResponse.getMessage(), containsString("required"));
        
        System.out.println("✅ Missing password correctly rejected");
    }
    
    @Test(description = "Verify login fails with empty credentials", priority = 5)
    @Story("Login with empty credentials")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test verifies that login fails when both username and password are empty")
    public void testLoginWithEmptyCredentials() {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder().build();
        
        // Act
        Response response = authAPI.login(loginRequest);
        
        // Assert
        assertStatusCode(response, 400);
        assertErrorResponse(response, 400);
        
        System.out.println("✅ Empty credentials correctly rejected");
    }
}

