package com.example.resume.chat.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final RoomSessionRegistry registry;
    private final ObjectMapper om;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8); // room:xxx
            String roomId = channel.substring("room:".length());
            String payload = new String(message.getBody(), StandardCharsets.UTF_8);

            Set<WebSocketSession> sessions = registry.sessionsOf(roomId);
            for (WebSocketSession s : sessions) {
                if (s.isOpen()){
                    s.sendMessage(new TextMessage(payload));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}