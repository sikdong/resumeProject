package com.example.resume.evaluation.service;

import com.example.resume.common.annotation.LogExecutionTime;
import com.example.resume.common.kafka.email.EmailNotificationProducer;
import com.example.resume.cv.domain.ResumeInteraction;
import com.example.resume.cv.dto.EmailNotificationEvent;
import com.example.resume.cv.repository.jpa.ResumeInteractionRepository;
import com.example.resume.cv.repository.queryDSL.ResumeInteractionQueryDSLRepository;
import com.example.resume.cv.service.EmailService;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.cv.domain.Resume;
import com.example.resume.evaluation.dto.EvaluationDto;
import com.example.resume.evaluation.dto.EvaluationUpdateResponseDto;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.exception.CustomException;
import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.resume.exception.ErrorCode.CURRENT_USER_EQUALS_RESUME_OWNER;
import static com.example.resume.exception.ErrorCode.RESUME_NOT_FOUND;
import static com.example.resume.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService {
    private final ResumeRepository resumeRepository;
    private final EvaluationRepository evaluationRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final EmailNotificationProducer emailNotificationProducer;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ResumeInteractionQueryDSLRepository resumeInteractionQueryDSLRepository;
    private final ResumeInteractionRepository resumeInteractionRepository;

    @Transactional
    @LogExecutionTime
    public void evaluate(Long resumeId, EvaluationDto evaluationRequestDto, Long memberId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new CustomException(RESUME_NOT_FOUND));
        Member member = validateMember(memberId);

        if(resume.getMember().getId().equals(memberId)){
            throw new CustomException(CURRENT_USER_EQUALS_RESUME_OWNER);
        }
        ResumeInteraction resumeInteraction = validateResumeInteraction(resumeId, memberId, resume, member);
        resumeInteraction.markEvaluated();

        Evaluation evaluation = Evaluation.builder()
                .resume(resume)
                .score(evaluationRequestDto.getScore())
                .comment(evaluationRequestDto.getComment())
                .evaluator(member)
                .build();
        evaluationRepository.save(evaluation);
        sendMail(resume);
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
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private void sendMail(Resume resume) {
        if (resume.getIsMailSent()){
            String toEmail = resume.getMember().getEmail();
            String resumeTitle = resume.getTitle();
            //emailService.sendReviewNotification(toEmail, resumeTitle);
            applicationEventPublisher.publishEvent(new EmailNotificationEvent(toEmail, resumeTitle));
        }
    }

    @NotNull
    private ResumeInteraction validateResumeInteraction(Long resumeId, Long memberId, Resume resume, Member member) {
        ResumeInteraction resumeInteraction = resumeInteractionQueryDSLRepository.getResumeInteraction(memberId, resumeId);
        if(resumeInteraction == null){
            resumeInteraction = ResumeInteraction.builder()
                    .resume(resume)
                    .member(member)
                    .build();
            resumeInteractionRepository.save(resumeInteraction);
        }
        return resumeInteraction;
    }
}
