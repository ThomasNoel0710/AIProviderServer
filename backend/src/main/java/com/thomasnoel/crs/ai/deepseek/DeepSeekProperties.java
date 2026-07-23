package com.thomasnoel.crs.ai.deepseek;

import java.net.URI;
import java.time.Duration;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "deepseek")
public record DeepSeekProperties(
        @NotNull URI baseUrl,
        String apiKey,
        @NotBlank String model,
        @NotNull Duration timeout) {

    @AssertTrue(message = "deepseek.base-url must use HTTP or HTTPS")
    public boolean isBaseUrlHttp() {
        if (baseUrl == null) {
            return true;
        }

        return "http".equalsIgnoreCase(baseUrl.getScheme())
                || "https".equalsIgnoreCase(baseUrl.getScheme());
    }

    @AssertTrue(message = "deepseek.timeout must be greater than zero")
    public boolean isTimeoutPositive() {
        return timeout == null || (!timeout.isZero() && !timeout.isNegative());
    }

    @Override
    public String toString() {
        String apiKeyStatus = apiKey == null || apiKey.isBlank()
                ? "<not configured>"
                : "<redacted>";

        return "DeepSeekProperties[baseUrl=%s, apiKey=%s, model=%s, timeout=%s]"
                .formatted(baseUrl, apiKeyStatus, model, timeout);
    }
}
