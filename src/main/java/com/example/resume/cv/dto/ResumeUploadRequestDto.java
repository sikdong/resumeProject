package com.example.resume.cv.dto;

public record ResumeUploadRequestDto(
        String fileName,
        String content,
        String title
) {
}
