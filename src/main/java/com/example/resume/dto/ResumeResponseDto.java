package com.example.resume.dto;

import com.example.resume.domain.Resume;
import com.example.resume.domain.User;

import java.time.LocalDateTime;

public record ResumeResponseDto(
        Long id,
        String title,
        String fileUrl,
        LocalDateTime createAt,
        Integer score,
        User user
) {
    public static ResumeResponseDto fromEntity(Resume resume) {
        return new ResumeResponseDto(
                resume.getId(),
                resume.getTitle(),
                resume.getFileUrl(),
                resume.getCreatedAt(),
                resume.getScore(),
                resume.getUser()
        );
    }
}
