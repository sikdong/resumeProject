package com.example.resume.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    public enum Type {JOIN, MESSAGE, LEAVE}

    private Type type;
    private String roomId;
    private String senderId;
    private String text;
    private long createdAt;
}
