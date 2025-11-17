package com.booklibrary.api;

import com.booklibrary.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Base API class providing common functionality for all API clients
 */
public abstract class BaseAPI {
    
    protected static final String BASE_URI = ConfigManager.getConfig().baseUrl();
    protected static final int DEFAULT_TIMEOUT = ConfigManager.getConfig().defaultTimeout();
    
    static {
        RestAssured.baseURI = BASE_URI;
    }
    
    /**
     * Returns a basic request specification with common headers and content type
     */
    protected RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();
    }
    
    /**
     * Returns a request specification with authentication token
     */
    protected RequestSpecification getAuthenticatedRequestSpec(String token) {
        return getRequestSpec()
                .header("Authorization", "Bearer " + token);
    }
}

