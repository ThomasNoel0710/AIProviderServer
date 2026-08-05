package com.thomasnoel.crs.ai;

public record ChatModelMessage(
    ChatModelRole role,
    String content
) {
}
