package com.booklibrary.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyzer for flaky tests
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 2;
    
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println("Retrying test: " + result.getName() + 
                             " for the " + retryCount + " time(s).");
            return true;
        }
        return false;
    }
}

