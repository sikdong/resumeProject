package com.example.resume.evaluation.controller;

import com.example.resume.evaluation.dto.EvaluationRequestDto;
import com.example.resume.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/{resumeId}")
    public ResponseEntity<Void> evaluate(
            @PathVariable Long resumeId,
            @RequestBody EvaluationRequestDto evaluationRequestDto){
        evaluationService.evaluate(resumeId, evaluationRequestDto);
        return ResponseEntity.ok().build();
    }
}