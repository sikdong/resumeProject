package com.example.resume.evaluation.dto;

import com.example.resume.evaluation.domain.Evaluation;

import java.time.LocalDateTime;

public record EvaluationSummaryResponseDto(
        Long id,
        Double score,
        LocalDateTime evaluatedAt,
        String comment,
        String memberName
) {
    public static EvaluationSummaryResponseDto fromEntity(Evaluation evaluation){
        return new EvaluationSummaryResponseDto(
                evaluation.getId(),
                evaluation.getScore(),
                evaluation.getCreatedAt(),
                evaluation.getComment(),
                evaluation.getEvaluator().getName()
        );
    }
}