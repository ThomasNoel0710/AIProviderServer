package com.thomasnoel.crs.ai;

public record ChatModelDefinition (
    ModelProvider provider,
    ChatProtocol protocol,
    String modelId,
    String displayName
){
}
