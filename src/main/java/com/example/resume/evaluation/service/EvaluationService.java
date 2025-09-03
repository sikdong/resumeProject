package com.example.resume.evaluation.service;

import com.example.resume.common.annotation.LogExecutionTime;
import com.example.resume.cv.service.EmailService;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.cv.domain.Resume;
import com.example.resume.evaluation.dto.EvaluationDto;
import com.example.resume.evaluation.dto.EvaluationUpdateResponseDto;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final ResumeRepository resumeRepository;
    private final EvaluationRepository evaluationRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    @Transactional
    @LogExecutionTime
    public void evaluate(Long resumeId, EvaluationDto evaluationRequestDto, Long memberId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("이력서가 존재하지 않습니다"));
        Member member = validateMember(memberId);
        Evaluation evaluation = Evaluation.builder()
                .resume(resume)
                .score(evaluationRequestDto.getScore())
                .comment(evaluationRequestDto.getComment())
                .evaluator(member)
                .build();
        sendMail(resume);
        evaluationRepository.save(evaluation);
    }

    private void sendMail(Resume resume) {
        if (!resume.getIsMailSent()){
            return;
        }
        String toEmail = resume.getMember().getEmail();
        String resumeTitle = resume.getTitle();
        emailService.sendReviewNotification(toEmail, resumeTitle);
    }

    @Transactional
    public void deleteEvaluation(Long evaluationId) {
        evaluationRepository.deleteById(evaluationId);
    }

    @Transactional
    public void update(Long evaluationId, EvaluationDto evaluationRequestDto, Long memberId) {
        validateMember(memberId);
        evaluationRepository.findById(evaluationId)
                .ifPresent(evaluation -> {
                    evaluation.setScore(evaluationRequestDto.getScore());
                    evaluation.setComment(evaluationRequestDto.getComment());
                });
    }

    public EvaluationDto getEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("평가가 존재하지 않습니다 === " + evaluationId));
        return EvaluationDto.fromEntity(evaluation);
    }

    public List<EvaluationUpdateResponseDto> getMyEvaluations(Long memberId) {
        return evaluationRepository.findByMemberIdWithResume(memberId)
                .stream()
                .map(EvaluationUpdateResponseDto::fromEntity)
                .toList();
    }

    /********private method*********/
    private Member validateMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
    }
}
