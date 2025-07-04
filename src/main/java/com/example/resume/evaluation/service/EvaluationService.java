package com.example.resume.evaluation.service;

import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.cv.domain.Resume;
import com.example.resume.evaluation.dto.EvaluationRequestDto;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final ResumeRepository resumeRepository;
    private final EvaluationRepository evaluationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void evaluate(Long resumeId, EvaluationRequestDto evaluationRequestDto, Long memberId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("이력서가 존재하지 않습니다"));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
        Evaluation evaluation = Evaluation.builder()
                .resume(resume)
                .score(evaluationRequestDto.getScore())
                .comment(evaluationRequestDto.getComment())
                .evaluator(member)
                .build();
        evaluationRepository.save(evaluation);
    }
}
