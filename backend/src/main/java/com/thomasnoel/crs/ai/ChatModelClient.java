package com.thomasnoel.crs.ai;
import java.util.List;

public interface ChatModelClient {
    ChatProtocol protocol();
    String chat(
        String modelId,
        List<ChatModelMessage> messages
    );
}
