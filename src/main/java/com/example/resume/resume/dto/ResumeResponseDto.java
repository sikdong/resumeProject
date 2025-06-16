package com.example.resume.resume.dto;

import com.example.resume.evaluation.dto.EvaluationResponseDto;
import com.example.resume.user.dto.MemberDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Setter
public class ResumeResponseDto {
        private Long id;
        private String title;
        private String fileUrl;
        private String keyword;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createAt;
        private double averageScore;
        private int commentSize;
        @JsonInclude(NON_EMPTY)
        private List<EvaluationResponseDto> evaluations;
        @JsonInclude(NON_EMPTY)
        private MemberDto member;

        @Builder
        public ResumeResponseDto(Long id, String title, String fileUrl, String keyword, LocalDateTime createAt, Double averageScore, int commentSize, List<EvaluationResponseDto> evaluations, MemberDto member) {
                this.id = id;
                this.title = title;
                this.fileUrl = fileUrl;
                this.keyword = keyword;
                this.createAt = createAt;
                this.averageScore = averageScore;
                this.commentSize = commentSize;
                this.evaluations = evaluations;
                this.member = member;
        }
}
