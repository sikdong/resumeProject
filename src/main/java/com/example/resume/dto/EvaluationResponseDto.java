package com.example.resume.dto;

import com.example.resume.domain.Evaluation;

import java.time.LocalDateTime;

public record EvaluationResponseDto(
        Long id,
        Double score,
        LocalDateTime evaluatedAt,
        String comment
) {
    public static EvaluationResponseDto fromEntity(Evaluation evaluation){
        return new EvaluationResponseDto(
                evaluation.getId(),
                evaluation.getScore(),
                evaluation.getCreatedAt(),
                evaluation.getComment()
        );
    }
}