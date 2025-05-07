package com.example.resume.dto;

import java.time.LocalDateTime;

public record EvaluationResponseDto(
        Long id,
        Long resumeId,
        Integer score,
        LocalDateTime evaluatedAt
) {}