package com.example.resume.evaluation.controller;

import com.example.resume.common.MemberUtil;
import com.example.resume.evaluation.dto.EvaluationDto;
import com.example.resume.evaluation.dto.EvaluationUpdateResponseDto;
import com.example.resume.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/resumes/{resumeId}/evaluations")
    public ResponseEntity<Void> createEvaluation(
            @PathVariable Long resumeId,
            @RequestBody EvaluationDto evaluationRequestDto,
            Authentication authentication) {
        Long memberId = MemberUtil.getMemberId(authentication);
        evaluationService.evaluate(resumeId, evaluationRequestDto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/evaluations/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long evaluationId) {
        evaluationService.deleteEvaluation(evaluationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/evaluations/{evaluationId}")
    public ResponseEntity<Void> updateEvaluation(
            @PathVariable Long evaluationId,
            @RequestBody EvaluationDto evaluationRequestDto,
            Authentication authentication) {
        Long memberId = MemberUtil.getMemberId(authentication);
        evaluationService.update(evaluationId, evaluationRequestDto, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evaluations/{evaluationId}")
    public ResponseEntity<EvaluationDto> getEvaluation(@PathVariable Long evaluationId) {
        EvaluationDto evaluationDto = evaluationService.getEvaluation(evaluationId);
        return ResponseEntity.ok(evaluationDto);
    }

    @GetMapping("/evaluations/me")
    public ResponseEntity<List<EvaluationUpdateResponseDto>> getMyEvaluation(Authentication authentication) {
        Long memberId = MemberUtil.getMemberId(authentication);
        List<EvaluationUpdateResponseDto> evaluationResponseDtos = evaluationService.getMyEvaluations(memberId);
        return ResponseEntity.ok(evaluationResponseDtos);
    }
}
