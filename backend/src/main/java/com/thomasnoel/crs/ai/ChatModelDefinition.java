package com.thomasnoel.crs.ai;

public record ChatModelDefinition (
    ModelProvider provider,
    String modelId,
    String displayName
){
}
