package com.thomasnoel.crs.ai;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ModelCatalog {
    private static final List<ChatModelDefinition> MODELS = List.of(
            new ChatModelDefinition(
                    ModelProvider.DEEPSEEK,
                    ChatProtocol.OPENAI_CHAT_COMPLETIONS,
                    "deepseek-v4-flash",
                    "DeepSeek-V4-Flash"
            ),
            new ChatModelDefinition(
                    ModelProvider.DEEPSEEK,
                    ChatProtocol.OPENAI_CHAT_COMPLETIONS,
                    "deepseek-v4-pro",
                    "DeepSeek-V4-Pro"
            )
    );

    public List<ChatModelDefinition> getModels() {
        return MODELS;
    }

    public boolean supports(
            ModelProvider provider,
            String modelId
    ) {
        return MODELS.stream()
                .anyMatch(model ->
                        model.provider() == provider
                                && model.modelId().equals(modelId)
                );
    }

    public ChatModelDefinition getModel(
            ModelProvider provider,
            String modelId
    ) {
        return MODELS.stream()
                .filter(model ->
                        model.provider() == provider
                                && model.modelId().equals(modelId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedModelException(provider, modelId)
                );
    }
}
