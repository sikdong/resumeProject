package com.example.resume.evaluation.dto;

import com.example.resume.evaluation.domain.Evaluation;

import java.time.LocalDateTime;

public record EvaluationResponseDto(
        Long id,
        Double score,
        LocalDateTime evaluatedAt,
        String comment,
        String memberName
) {
    public static EvaluationResponseDto fromEntity(Evaluation evaluation){
        return new EvaluationResponseDto(
                evaluation.getId(),
                evaluation.getScore(),
                evaluation.getCreatedAt(),
                evaluation.getComment(),
                evaluation.getEvaluator().getName()
        );
    }
}