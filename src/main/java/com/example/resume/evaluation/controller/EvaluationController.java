package com.example.resume.evaluation.controller;

import com.example.resume.common.MemberUtil;
import com.example.resume.evaluation.dto.EvaluationRequestDto;
import com.example.resume.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/{resumeId}")
    public ResponseEntity<Void> evaluate(
            @PathVariable Long resumeId,
            @RequestBody EvaluationRequestDto evaluationRequestDto,
            Authentication authentication){
        Long memberId = MemberUtil.getMemberId(authentication);
        evaluationService.evaluate(resumeId, evaluationRequestDto, memberId);
        return ResponseEntity.ok().build();
    }
}