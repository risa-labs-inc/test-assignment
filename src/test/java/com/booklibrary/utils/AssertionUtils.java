package com.booklibrary.utils;

import io.restassured.response.Response;
import org.testng.Assert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Utility class for common API response assertions
 */
public class AssertionUtils {
    
    /**
     * Assert response status code
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode,
                String.format("Expected status code %d but got %d", expectedStatusCode, actualStatusCode));
    }
    
    /**
     * Assert response body contains field
     */
    public static void assertResponseContainsField(Response response, String fieldPath) {
        assertThat("Response should contain field: " + fieldPath,
                response.jsonPath().get(fieldPath), is(notNullValue()));
    }
    
    /**
     * Assert response body field has expected value
     */
    public static void assertResponseFieldEquals(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        assertThat(String.format("Field '%s' should equal '%s'", fieldPath, expectedValue),
                actualValue, equalTo(expectedValue));
    }
    
    /**
     * Assert response time is within acceptable limit
     */
    public static void assertResponseTime(Response response, long maxTimeInMs) {
        long responseTime = response.getTime();
        Assert.assertTrue(responseTime <= maxTimeInMs,
                String.format("Response time %d ms exceeded max allowed time %d ms", responseTime, maxTimeInMs));
    }
    
    /**
     * Assert success response
     */
    public static void assertSuccessResponse(Response response) {
        assertStatusCode(response, 200);
        assertResponseFieldEquals(response, "success", true);
    }
    
    /**
     * Assert error response with status code
     */
    public static void assertErrorResponse(Response response, int expectedStatusCode) {
        assertStatusCode(response, expectedStatusCode);
        assertResponseContainsField(response, "error");
        assertResponseContainsField(response, "message");
    }
    
    /**
     * Assert response contains error message
     */
    public static void assertErrorMessage(Response response, String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        assertThat("Error message should contain expected text",
                actualMessage, containsString(expectedMessage));
    }
    
    /**
     * Assert response is successful and contains data
     */
    public static void assertSuccessResponseWithData(Response response) {
        assertSuccessResponse(response);
        assertResponseContainsField(response, "data");
    }
}

