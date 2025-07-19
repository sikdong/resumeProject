package com.example.resume.evaluation.dto;

import com.example.resume.evaluation.domain.Evaluation;

import java.time.LocalDateTime;

public record EvaluationUpdateResponseDto(
        Long id,
        Double score,
        LocalDateTime evaluatedAt,
        String comment,
        String resumeTitle,
        Long resumeId
) {
    public static EvaluationUpdateResponseDto fromEntity(Evaluation evaluation){
        return new EvaluationUpdateResponseDto(
                evaluation.getId(),
                evaluation.getScore(),
                evaluation.getCreatedAt(),
                evaluation.getComment(),
                evaluation.getResume().getTitle(),
                evaluation.getResume().getId()
        );
    }
}
