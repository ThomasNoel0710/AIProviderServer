package com.thomasnoel.crs.api.model;

import java.util.List;

import com.thomasnoel.crs.ai.ChatModelDefinition;
import com.thomasnoel.crs.ai.ChatProtocol;
import com.thomasnoel.crs.ai.ModelCatalog;
import com.thomasnoel.crs.ai.ModelProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModelController.class)
class ModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelCatalog modelCatalog;

    @Test
    void returnsAvailableModels() throws Exception {
        when(modelCatalog.getModels()).thenReturn(
                List.of(
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
                )
        );

        mockMvc.perform(get("/api/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].provider").value("DEEPSEEK"))
                .andExpect(
                        jsonPath("$[0].modelId")
                                .value("deepseek-v4-flash")
                )
                .andExpect(
                        jsonPath("$[0].protocol")
                                .value("OPENAI_CHAT_COMPLETIONS")
                )
                .andExpect(
                        jsonPath("$[1].displayName")
                                .value("DeepSeek-V4-Pro")
                );
    }
}
