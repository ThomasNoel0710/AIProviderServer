package com.thomasnoel.crs.ai;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedModelException extends RuntimeException {
    public UnsupportedModelException(
        ModelProvider provider,
        String modelId
    ) {
        super("Unsupported model provider: " + provider + ", model ID: " + modelId);
    }
}