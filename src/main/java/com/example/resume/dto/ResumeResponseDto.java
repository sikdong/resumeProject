package com.example.resume.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ResumeResponseDto(
        Long id,
        String title,
        String fileUrl,
        LocalDateTime createAt,
        Double averageScore,
        int commentSize,
        List<EvaluationResponseDto> evaluations
) {

}
