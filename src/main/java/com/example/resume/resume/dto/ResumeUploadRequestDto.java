package com.example.resume.resume.dto;

public record ResumeUploadRequestDto(
        String fileName,
        String content,
        String title
) {
}
