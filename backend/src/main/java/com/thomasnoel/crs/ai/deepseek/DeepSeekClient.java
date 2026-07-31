package com.thomasnoel.crs.ai.deepseek;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.thomasnoel.crs.ai.deepseek.dto.DeepSeekChatRequest;
import com.thomasnoel.crs.ai.deepseek.dto.DeepSeekChatResponse;
import com.thomasnoel.crs.ai.deepseek.dto.DeepSeekMessage;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class DeepSeekClient {

    private final DeepSeekProperties properties;
    private final JsonMapper jsonMapper;
    private final HttpClient httpClient;

    public DeepSeekClient(
            DeepSeekProperties properties,
            JsonMapper jsonMapper
    ) {
        this.properties = properties;
        this.jsonMapper = jsonMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.timeout())
                .build();
    }

    private DeepSeekChatRequest buildRequest(
            List<DeepSeekMessage> messages
    ) {
        return new DeepSeekChatRequest(
            properties.model(),
            messages,
            new DeepSeekChatRequest.Thinking("disabled"),
            512,
            false
        );
    }

    private String buildRequestJson(List<DeepSeekMessage> messages) {
        DeepSeekChatRequest request = buildRequest(messages);

        try {
            return jsonMapper.writeValueAsString(request);
        } catch (JacksonException exception) {
            throw new DeepSeekException(
                "Failed to serialize DeepSeek request to JSON",
                exception
            );
        }
    }

    private HttpRequest buildHttpRequest(String requestJson) {
        URI endpoint = properties.baseUrl().resolve("/chat/completions");
        return HttpRequest.newBuilder().
                uri(endpoint).
                timeout(properties.timeout()).
                header("Authorization", "Bearer " + properties.apiKey()).
                header("Content-Type", "application/json").
                POST(HttpRequest.BodyPublishers.ofString(requestJson)).
                build();
    }

    private String extractAnswer(String responseJson) {
        DeepSeekChatResponse response;

        try{
            response = jsonMapper.readValue(
                responseJson,
                DeepSeekChatResponse.class
            );
        } catch (JacksonException exception) {
            throw new DeepSeekException(
                "Failed to deserialize DeepSeek response from JSON",
                exception
            );
        }

        if (response == null || 
            response.choices() == null || 
            response.choices().isEmpty()) {
            throw new DeepSeekException(
                "DeepSeek response is missing choices"
            );
        }

        DeepSeekChatResponse.Choice choice = response.choices().get(0);
        DeepSeekMessage message = choice == null ? null : choice.message();

        if (message == null || message.content() == null || message.content().isBlank()) {
            throw new DeepSeekException(
                "DeepSeek response is missing message content"
            );
        }

        return message.content();
    }

    public String chat(String userMessage) {
        return chat(List.of(
                new DeepSeekMessage("user", userMessage)
        ));
    }

    public String chat(List<DeepSeekMessage> messages) {
        String requestJson = buildRequestJson(messages);
        HttpRequest request = buildHttpRequest(requestJson);

        HttpResponse<String> response;

        try {
            response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DeepSeekException(
                "DeepSeek request interrupted", 
                exception
            );
        } catch (IOException exception) {
            throw new DeepSeekException(
                "DeepSeek request failed", 
                exception
            );
        }

        if (response.statusCode() / 100 != 2) {
            throw new DeepSeekException(
                "DeepSeek request failed with status code: " + response.statusCode()
            );
        }

        return extractAnswer(response.body());
    }
}
