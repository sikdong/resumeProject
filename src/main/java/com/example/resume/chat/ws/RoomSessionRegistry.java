package com.example.resume.chat.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomSessionRegistry {
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessionRooms = new ConcurrentHashMap<>();

    public void join(String roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionRooms.computeIfAbsent(session.getId(), k -> ConcurrentHashMap.newKeySet()).add(roomId);
    }

    public void leaveAll(WebSocketSession session) {
        Set<String> rooms = sessionRooms.getOrDefault(session.getId(), Collections.emptySet());
        for (String roomId : rooms) {
            Set<WebSocketSession> set = roomSessions.get(roomId);
            if (set != null){
                set.remove(session);
            }
        }
        sessionRooms.remove(session.getId());
    }

    public Set<WebSocketSession> sessionsOf(String roomId) {
        return roomSessions.getOrDefault(roomId, Collections.emptySet());
    }
}
