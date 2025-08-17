package com.example.resume.cv.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ResumeRecentlyViewedResponseDto(
        Long resumeId,
        String title
) {
}
