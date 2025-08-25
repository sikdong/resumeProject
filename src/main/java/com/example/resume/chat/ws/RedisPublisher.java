package com.example.resume.chat.ws;

import com.example.resume.chat.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {
    private final StringRedisTemplate template;
    private final ObjectMapper om;

    public void publish(String roomId, ChatMessage msg) {
        try {
            String payload = om.writeValueAsString(msg);
            template.convertAndSend("room:" + roomId, payload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
