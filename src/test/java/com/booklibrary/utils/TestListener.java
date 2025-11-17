package com.booklibrary.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for test lifecycle events
 */
public class TestListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("\n▶️ Starting test: " + result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ Test PASSED: " + result.getName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("❌ Test FAILED: " + result.getName());
        System.out.println("   Reason: " + result.getThrowable().getMessage());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️ Test SKIPPED: " + result.getName());
    }
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📝 Test Suite Started: " + context.getName());
        System.out.println("=".repeat(80));
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 Test Suite Finished: " + context.getName());
        System.out.println("   Passed: " + context.getPassedTests().size());
        System.out.println("   Failed: " + context.getFailedTests().size());
        System.out.println("   Skipped: " + context.getSkippedTests().size());
        System.out.println("=".repeat(80) + "\n");
    }
}

