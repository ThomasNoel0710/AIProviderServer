package com.thomasnoel.crs;

import java.net.URI;
import java.time.Duration;

import com.thomasnoel.crs.ai.deepseek.DeepSeekProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = "DEEPSEEK_API_KEY=test-secret")
class CrsBackendApplicationTests {

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @Test
    void contextLoads() {
    }

    @Test
    void bindsDeepSeekConfiguration() {
        assertEquals(URI.create("https://api.deepseek.com"), deepSeekProperties.baseUrl());
        assertEquals("test-secret", deepSeekProperties.apiKey());
        assertEquals("deepseek-v4-flash", deepSeekProperties.model());
        assertEquals(Duration.ofSeconds(30), deepSeekProperties.timeout());
        assertFalse(deepSeekProperties.toString().contains("test-secret"));
    }
}
