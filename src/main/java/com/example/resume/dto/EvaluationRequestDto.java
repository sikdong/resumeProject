package com.example.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EvaluationRequestDto {
    private Long resumeId;
    private Double score;
    private String comment;
}