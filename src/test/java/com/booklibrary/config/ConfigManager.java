package com.booklibrary.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Configuration manager to access test configuration properties
 */
public class ConfigManager {
    
    private static final TestConfig CONFIG = ConfigFactory.create(TestConfig.class);
    
    private ConfigManager() {
        // Private constructor to prevent instantiation
    }
    
    public static TestConfig getConfig() {
        return CONFIG;
    }
}

