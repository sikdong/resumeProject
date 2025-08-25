package com.example.resume.chat.ws;

import com.example.resume.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final RoomSessionRegistry registry;
    private final RedisPublisher publisher;
    private final ObjectMapper om;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessage incoming = om.readValue(message.getPayload(), ChatMessage.class);

        switch (incoming.getType()) {
            case JOIN -> handleJoinMessage(incoming, session);
            case LEAVE -> handleLeaveMessage(session);
            case MESSAGE -> handleChatMessage(incoming, session);
        }
    }

    private void handleJoinMessage(ChatMessage message, WebSocketSession session) {
        registry.join(message.getRoomId(), session);
    }

    private void handleLeaveMessage(WebSocketSession session) {
        registry.leaveAll(session);
    }

    private void handleChatMessage(ChatMessage message, WebSocketSession session) {
        String senderId = getSenderIdFromSession(session);
        message.setSenderId(senderId);
        message.setCreatedAt(System.currentTimeMillis());
        publisher.publish(message.getRoomId(), message);
    }

    private String getSenderIdFromSession(WebSocketSession session) {
        return (String) session.getAttributes().get("USER_ID");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.leaveAll(session);
    }
}