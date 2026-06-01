package com.planningpoker.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * WebSocket endpoint — clients connect here to receive real-time events.
 *
 * URL: ws://localhost:8080/ws/sessions/{sessionId}
 *
 * This class has two responsibilities:
 *  1. Accept WebSocket connections (handled automatically by Quarkus via @WebSocket)
 *  2. Expose broadcast() so SessionService can push events to all clients in a session
 */
@WebSocket(path = "/ws/sessions/{sessionId}")
@ApplicationScoped
public class SessionSocket {

    // OpenConnections is provided by Quarkus — tracks every currently open WS connection
    @Inject
    OpenConnections openConnections;

    // Jackson ObjectMapper — serializes WsEvent to JSON string for sending over the wire
    @Inject
    ObjectMapper objectMapper;

    @OnOpen
    public void onOpen() {
        // Quarkus automatically adds the connection to OpenConnections.
        // Nothing extra needed here.
    }

    @OnClose
    public void onClose() {
        // Quarkus automatically removes the connection from OpenConnections.
        // PARTICIPANT_LEFT is handled by the explicit POST /leave REST call instead.
    }

    /**
     * Broadcasts a JSON event to every client currently connected to the given session.
     *
     * Called by SessionService after every state mutation.
     *
     * @param sessionId the session to broadcast to
     * @param event     the event to send
     */
    public void broadcast(String sessionId, WsEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            openConnections.stream()
                .filter(c -> sessionId.equals(c.pathParam("sessionId")))
                .forEach(c -> c.sendTextAndAwait(json));

        } catch (JsonProcessingException e) {
            // Serialization failure — should never happen for our simple event types
        }
    }
}
