package com.booklibrary.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "file:src/test/resources/test.properties"
})
public interface TestConfig extends Config {
    
    @Key("base.url")
    @DefaultValue("http://localhost:3000")
    String baseUrl();
    
    @Key("auth.username")
    @DefaultValue("admin")
    String authUsername();
    
    @Key("auth.password")
    @DefaultValue("test123")
    String authPassword();
    
    @Key("timeout.default")
    @DefaultValue("10000")
    Integer defaultTimeout();
    
    @Key("retry.max.attempts")
    @DefaultValue("3")
    Integer maxRetryAttempts();
    
    @Key("logging.enabled")
    @DefaultValue("true")
    boolean loggingEnabled();
}

