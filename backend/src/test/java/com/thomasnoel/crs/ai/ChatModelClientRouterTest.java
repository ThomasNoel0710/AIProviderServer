package com.thomasnoel.crs.ai;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatModelClientRouterTest {

    @Test
    void returnsClientMatchingProtocol() {
        ChatModelClient client = new FakeChatModelClient(
                ChatProtocol.OPENAI_CHAT_COMPLETIONS
        );
        ChatModelClientRouter router =
                new ChatModelClientRouter(List.of(client));

        ChatModelClient result = router.getClient(
                ChatProtocol.OPENAI_CHAT_COMPLETIONS
        );

        assertSame(client, result);
    }

    @Test
    void throwsWhenNoClientMatchesProtocol() {
        ChatModelClientRouter router =
                new ChatModelClientRouter(List.of());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> router.getClient(
                        ChatProtocol.OPENAI_CHAT_COMPLETIONS
                )
        );

        assertTrue(
                exception.getMessage().contains(
                        ChatProtocol.OPENAI_CHAT_COMPLETIONS.name()
                )
        );
    }

    @Test
    void throwsWhenMultipleClientsMatchProtocol() {
        ChatModelClient firstClient = new FakeChatModelClient(
                ChatProtocol.OPENAI_CHAT_COMPLETIONS
        );
        ChatModelClient secondClient = new FakeChatModelClient(
                ChatProtocol.OPENAI_CHAT_COMPLETIONS
        );
        ChatModelClientRouter router =
                new ChatModelClientRouter(
                        List.of(firstClient, secondClient)
                );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> router.getClient(
                        ChatProtocol.OPENAI_CHAT_COMPLETIONS
                )
        );

        assertTrue(
                exception.getMessage().contains(
                        ChatProtocol.OPENAI_CHAT_COMPLETIONS.name()
                )
        );
    }

    private static class FakeChatModelClient
            implements ChatModelClient {

        private final ChatProtocol protocol;

        private FakeChatModelClient(ChatProtocol protocol) {
            this.protocol = protocol;
        }

        @Override
        public ChatProtocol protocol() {
            return protocol;
        }

        @Override
        public String chat(
                String modelId,
                List<ChatModelMessage> messages
        ) {
            throw new UnsupportedOperationException(
                    "chat() is not used by router tests"
            );
        }
    }
}