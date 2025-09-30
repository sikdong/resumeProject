package com.example.resume.cv.dto;

import com.example.resume.evaluation.dto.EvaluationSummaryResponseDto;
import com.example.resume.user.dto.MemberDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
public class ResumeResponseDto {
        private Long id;
        private String title;
        private String fileUrl;
        @JsonInclude(NON_NULL)
        private String keyword;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createAt;
        private double averageScore;
        private int commentSize;
        @JsonInclude(NON_EMPTY)
        private List<EvaluationSummaryResponseDto> evaluations;
        @JsonInclude(NON_EMPTY)
        private MemberDto member;
        private Long viewCount;
        private String comment;
        private Boolean isViewed;
        private Boolean isEvaluated;

        @Builder
        public ResumeResponseDto(
                Long id,
                String title,
                String fileUrl,
                String keyword,
                LocalDateTime createAt,
                Double averageScore,
                int commentSize,
                List<EvaluationSummaryResponseDto> evaluations,
                MemberDto member,
                Long viewCount,
                String comment) {
                this.id = id;
                this.title = title;
                this.fileUrl = fileUrl;
                this.keyword = keyword;
                this.createAt = createAt;
                this.averageScore = averageScore;
                this.commentSize = commentSize;
                this.evaluations = evaluations;
                this.member = member;
                this.viewCount = viewCount == null ? 0L : viewCount;
                this.comment = comment;
        }
}
