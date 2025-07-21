package com.example.resume.evaluation.dto;

import com.example.resume.evaluation.domain.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EvaluationDto {
    private Double score;
    private String comment;

    public static EvaluationDto fromEntity(Evaluation evaluation){
        return new EvaluationDto(evaluation.getScore(), evaluation.getComment());
    }
}