package com.thomasnoel.crs.api.conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.thomasnoel.crs.ai.ModelProvider;
import com.thomasnoel.crs.ai.UnsupportedModelException;
import com.thomasnoel.crs.api.conversation.dto.ConversationDetailResponse;
import com.thomasnoel.crs.api.conversation.dto.ConversationSummaryResponse;
import com.thomasnoel.crs.api.conversation.dto.MessageResponse;
import com.thomasnoel.crs.conversation.ConversationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    private static final UUID CONVERSATION_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MESSAGE_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final Instant CREATED_AT =
            Instant.parse("2026-07-28T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationService conversationService;

    @Test
    void createsConversation() throws Exception {
        when(
                conversationService.createConversation(
                        ModelProvider.DEEPSEEK,
                        "deepseek-v4-flash"
                )
        )
                .thenReturn(conversationSummary());

        mockMvc.perform(
                        post("/api/conversations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "provider": "DEEPSEEK",
                                          "modelId": "deepseek-v4-flash"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(CONVERSATION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.title")
                                .value("New conversation")
                )
                .andExpect(
                        jsonPath("$.provider").value("DEEPSEEK")
                )
                .andExpect(
                        jsonPath("$.modelId").value("deepseek-v4-flash")
                );
    }

    @Test
    void rejectsBlankModelId() throws Exception {
        mockMvc.perform(
                        post("/api/conversations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "provider": "DEEPSEEK",
                                          "modelId": " "
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsUnsupportedModel() throws Exception {
        doThrow(
                new UnsupportedModelException(
                        ModelProvider.DEEPSEEK,
                        "unknown"
                )
        ).when(conversationService).createConversation(
                ModelProvider.DEEPSEEK,
                "unknown"
        );

        mockMvc.perform(
                        post("/api/conversations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "provider": "DEEPSEEK",
                                          "modelId": "unknown"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsConversations() throws Exception {
        when(conversationService.listConversations())
                .thenReturn(List.of(conversationSummary()));

        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(CONVERSATION_ID.toString()))
                .andExpect(jsonPath("$[0].title")
                        .value("New conversation"));
    }

    @Test
    void returnsConversationWithMessages() throws Exception {
        MessageResponse message = messageResponse();
        when(conversationService.getConversation(CONVERSATION_ID))
                .thenReturn(
                        new ConversationDetailResponse(
                                CONVERSATION_ID,
                                "Spring Boot",
                                ModelProvider.DEEPSEEK,
                                "deepseek-v4-pro",
                                CREATED_AT,
                                CREATED_AT.plusSeconds(1),
                                List.of(message)
                        )
                );

        mockMvc.perform(
                        get("/api/conversations/{conversationId}",
                                CONVERSATION_ID)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot"))
                .andExpect(jsonPath("$.modelId").value("deepseek-v4-pro"))
                .andExpect(jsonPath("$.messages[0].role").value("assistant"))
                .andExpect(
                        jsonPath("$.messages[0].content")
                                .value("Your name is Thomas.")
                );
    }

    @Test
    void deletesConversation() throws Exception {
        mockMvc.perform(
                        delete(
                                "/api/conversations/{conversationId}",
                                CONVERSATION_ID
                        )
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(conversationService).deleteConversation(CONVERSATION_ID);
    }

    @Test
    void returnsNotFoundWhenDeletingMissingConversation() throws Exception {
        doThrow(new ConversationNotFoundException(CONVERSATION_ID))
                .when(conversationService)
                .deleteConversation(CONVERSATION_ID);

        mockMvc.perform(
                        delete(
                                "/api/conversations/{conversationId}",
                                CONVERSATION_ID
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void allowsDeleteInCorsPreflight() throws Exception {
        mockMvc.perform(
                        options(
                                "/api/conversations/{conversationId}",
                                CONVERSATION_ID
                        )
                                .header(ORIGIN, "http://localhost:5173")
                                .header(ACCESS_CONTROL_REQUEST_METHOD, "DELETE")
                )
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                ACCESS_CONTROL_ALLOW_ORIGIN,
                                "http://localhost:5173"
                        )
                )
                .andExpect(
                        header().string(
                                ACCESS_CONTROL_ALLOW_METHODS,
                                org.hamcrest.Matchers.containsString("DELETE")
                        )
                );
    }

    @Test
    void sendsMessageToConversation() throws Exception {
        when(
                conversationService.sendMessage(
                        CONVERSATION_ID,
                        "What is my name?"
                )
        ).thenReturn(messageResponse());

        mockMvc.perform(
                        post(
                                "/api/conversations/{conversationId}/messages",
                                CONVERSATION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "message": "What is my name?"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MESSAGE_ID.toString()))
                .andExpect(jsonPath("$.sequenceNumber").value(2))
                .andExpect(jsonPath("$.role").value("assistant"))
                .andExpect(
                        jsonPath("$.content")
                                .value("Your name is Thomas.")
                );
    }

    @Test
    void renamesConversation() throws Exception {
        ConversationSummaryResponse renamed =
                new ConversationSummaryResponse(
                        CONVERSATION_ID,
                        "Java learning",
                        ModelProvider.DEEPSEEK,
                        "deepseek-v4-flash",
                        CREATED_AT,
                        CREATED_AT.plusSeconds(1)
                );
        when(
                conversationService.renameConversation(
                        CONVERSATION_ID,
                        "Java learning"
                )
        ).thenReturn(renamed);

        mockMvc.perform(
                        patch(
                                "/api/conversations/{conversationId}",
                                CONVERSATION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Java learning"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java learning"));
    }

    @Test
    void rejectsBlankConversationTitle() throws Exception {
        mockMvc.perform(
                        patch(
                                "/api/conversations/{conversationId}",
                                CONVERSATION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "   "
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsBlankMessage() throws Exception {
        mockMvc.perform(
                        post(
                                "/api/conversations/{conversationId}/messages",
                                CONVERSATION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "message": "   "
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    private ConversationSummaryResponse conversationSummary() {
        return new ConversationSummaryResponse(
                CONVERSATION_ID,
                "New conversation",
                ModelProvider.DEEPSEEK,
                "deepseek-v4-flash",
                CREATED_AT,
                CREATED_AT
        );
    }

    private MessageResponse messageResponse() {
        return new MessageResponse(
                MESSAGE_ID,
                2,
                "assistant",
                "Your name is Thomas.",
                CREATED_AT.plusSeconds(1)
        );
    }
}
