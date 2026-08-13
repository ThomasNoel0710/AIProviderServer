package com.thomasnoel.crs.ai;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ChatModelClientRouter {

    private final List<ChatModelClient> clients;

    public ChatModelClientRouter(
            List<ChatModelClient> clients
    ) {
        this.clients = List.copyOf(clients);
    }

    public ChatModelClient getClient(ChatProtocol protocol) {
        List<ChatModelClient> matchingClients = clients.stream()
            .filter(client -> client.protocol() == protocol)
            .toList();
        if (matchingClients.isEmpty()) {
            throw new IllegalStateException("No client found for protocol: " + protocol);
        }
        if (matchingClients.size() > 1) {
            throw new IllegalStateException("Multiple clients found for protocol: " + protocol);
        }
        return matchingClients.getFirst();
    }
}