package com.booklibrary.api;

import com.booklibrary.models.LoginRequest;
import com.booklibrary.models.LoginResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * API client for authentication endpoints
 */
public class AuthAPI extends BaseAPI {
    
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String LOGIN_ENDPOINT = AUTH_ENDPOINT + "/login";
    
    /**
     * Login with credentials and return Response
     */
    @Step("Login with username: {loginRequest.username}")
    public Response login(LoginRequest loginRequest) {
        return given()
                .spec(getRequestSpec())
                .body(loginRequest)
                .when()
                .post(LOGIN_ENDPOINT);
    }
    
    /**
     * Login and extract token directly
     */
    @Step("Login and get token for user: {loginRequest.username}")
    public String loginAndGetToken(LoginRequest loginRequest) {
        return login(loginRequest)
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class)
                .getToken();
    }
    
    /**
     * Login with default credentials and get token
     */
    @Step("Login with default credentials")
    public String loginWithDefaultCredentials() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("admin")
                .password("test123")
                .build();
        return loginAndGetToken(loginRequest);
    }
}

