package com.thomasnoel.crs.api.chat;

import com.thomasnoel.crs.ai.deepseek.DeepSeekClient;
import com.thomasnoel.crs.api.chat.dto.ChatRequest;
import com.thomasnoel.crs.api.chat.dto.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final DeepSeekClient deepSeekClient;

    public ChatService(DeepSeekClient deepSeekClient) {
        this.deepSeekClient = deepSeekClient;
    }

    public ChatResponse chat(ChatRequest request) {
        String answer = deepSeekClient.chat(request.message());
        return new ChatResponse(answer);
    }
}
