package com.thomasnoel.crs;

import java.net.URI;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import com.thomasnoel.crs.ai.deepseek.DeepSeekProperties;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeepSeekPropertiesTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsInvalidConfiguration() {
        DeepSeekProperties properties = new DeepSeekProperties(
                URI.create("ftp://api.example.com"),
                "",
                " ",
                Duration.ZERO);

        Set<String> invalidProperties = validator.validate(properties).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("baseUrlHttp", "model", "timeoutPositive"), invalidProperties);
    }
}
