package com.thomasnoel.crs.ai.deepseek;

public class DeepSeekException extends RuntimeException {

    public DeepSeekException(String message) {
        super(message);
    }

    public DeepSeekException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}