package com.thomasnoel.crs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.thomasnoel.crs.ai.deepseek.DeepSeekProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EnabledIfEnvironmentVariable(
        named = "RUN_DEEPSEEK_IT",
        matches = "true"
)
class DeepSeekConnectionTest {

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void connectsToDeepSeek() throws Exception {
        assertFalse(
                deepSeekProperties.apiKey() == null
                        || deepSeekProperties.apiKey().isBlank(),
                "Please configure DEEPSEEK_API_KEY in backend/.env"
        );

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(deepSeekProperties.timeout())
                .build();

        URI endpoint = deepSeekProperties.baseUrl()
                .resolve("/chat/completions");

        Map<String, Object> requestBody = Map.of(
                "model", deepSeekProperties.model(),
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", "Reply with one word: pong"
                        )
                ),
                "thinking", Map.of("type", "disabled"),
                "max_tokens", 16,
                "stream", false
        );

        String requestJson =
                jsonMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(endpoint)
                .timeout(deepSeekProperties.timeout())
                .header(
                        "Authorization",
                        "Bearer " + deepSeekProperties.apiKey()
                )
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(requestJson)
                )
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(
                200,
                response.statusCode(),
                () -> "DeepSeek returned: " + response.body()
        );

        JsonNode responseJson =
                jsonMapper.readTree(response.body());

        JsonNode choices =
                responseJson.path("choices");

        assertTrue(
                choices.isArray() && choices.size() > 0,
                "Response does not contain choices"
        );

        String answer = choices.get(0)
                .path("message")
                .path("content")
                .asString();

        assertFalse(
                answer.isBlank(),
                "DeepSeek returned an empty answer"
        );
    }
}
