package com.example.resume.evaluation.service;

import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.resume.domain.Resume;
import com.example.resume.evaluation.dto.EvaluationRequestDto;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final ResumeRepository resumeRepository;
    private final EvaluationRepository evaluationRepository;

    /*@Transactional
    public void evaluate(Long resumeId, EvaluationRequestDto evaluationRequestDto) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("이력서가 존재하지 않습니다"));
        Evaluation evaluation = Evaluation.builder()
                .resume(resume)
                .score(evaluationRequestDto.score())
                .comment(evaluationRequestDto.comment())
                .build();
        evaluationRepository.save(evaluation);
    }*/

    @Transactional
    public void evaluate(Long resumeId, EvaluationRequestDto evaluationRequestDto) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("이력서가 존재하지 않습니다"));
        Evaluation evaluation = Evaluation.builder()
                .resume(resume)
                .score(evaluationRequestDto.getScore())
                .comment(evaluationRequestDto.getComment())
                .build();
        evaluationRepository.save(evaluation);
    }
}
