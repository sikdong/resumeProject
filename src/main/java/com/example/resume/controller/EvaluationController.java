package com.example.resume.controller;

import com.example.resume.dto.EvaluationRequestDto;
import com.example.resume.service.EvaluationService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
        //FIXME TEMP
        evaluationService.evaluate(1L, evaluationRequestDto);
        //evaluationService.evaluate(resumeId, evaluationRequestDto);
        return ResponseEntity.ok().build();
    }
}