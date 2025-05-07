package com.example.resume.dto;

public record ResumeUploadRequestDto(
        String fileName,
        String content,
        String title
) {
}
