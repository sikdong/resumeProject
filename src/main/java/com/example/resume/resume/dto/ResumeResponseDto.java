package com.example.resume.resume.dto;

import com.example.resume.evaluation.dto.EvaluationResponseDto;
import com.example.resume.user.dto.MemberDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Setter
@AllArgsConstructor
public class ResumeResponseDto {
        private Long id;
        private String title;
        private String fileUrl;
        private LocalDateTime createAt;
        private Double averageScore;
        private int commentSize;
        @JsonInclude(NON_EMPTY)
        private List<EvaluationResponseDto> evaluations;
        @JsonInclude(NON_EMPTY)
        private MemberDto member;

        public ResumeResponseDto(Long id, String title, String fileUrl, LocalDateTime createAt,
                                 Double averageScore, int commentSize, List<EvaluationResponseDto> evaluations) {
                this(id, title, fileUrl, createAt, averageScore, commentSize, evaluations, null);
        }

}
