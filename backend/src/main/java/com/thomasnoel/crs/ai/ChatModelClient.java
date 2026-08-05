package com.thomasnoel.crs.ai;
import java.util.List;

public interface ChatModelClient {
    String chat(
        String modelId,
        List<ChatModelMessage> messages
    );
}
