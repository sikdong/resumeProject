package com.example.resume.resume.dto;

import com.example.resume.evaluation.dto.EvaluationResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

public record ResumeResponseDto(
        Long id,
        String title,
        String fileUrl,
        LocalDateTime createAt,
        Double averageScore,
        int commentSize,
        @JsonInclude(NON_EMPTY)
        List<EvaluationResponseDto> evaluations
) {

}
