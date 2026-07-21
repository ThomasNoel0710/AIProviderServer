package com.thomasnoel.crs.api.chat;

import com.thomasnoel.crs.api.chat.dto.ChatRequest;
import com.thomasnoel.crs.api.chat.dto.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    public ChatResponse chat(ChatRequest request) {
        String responseMessage = "This is a test response";
        return new ChatResponse(responseMessage);
    }
}
