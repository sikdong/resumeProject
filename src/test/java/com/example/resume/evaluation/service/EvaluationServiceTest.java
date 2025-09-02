package com.example.resume.evaluation.service;

import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.cv.service.EmailService;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.evaluation.dto.EvaluationDto;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EvaluationServiceTest {

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("평가를 등록한다")
    void shouldEvaluateSuccessfully() {
        Long resumeId = 1L;
        Long memberId = 1L;
        Resume resume = new Resume();
        Member member = new Member();
        EvaluationDto evaluationDto = new EvaluationDto();
        evaluationDto.setScore(10.0);
        evaluationDto.setComment("Good job!");

        resume.setMember(member);
        resume.setTitle("Test Resume");
        member.setEmail("test@example.com");

        Mockito.when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        Mockito.when(evaluationRepository.save(any(Evaluation.class))).thenReturn(new Evaluation());

        assertDoesNotThrow(() -> evaluationService.evaluate(resumeId, evaluationDto, memberId));
    }

    @Test
    @DisplayName("존재하지 않는 이력서에 대해 평가할때 오류를 발생시킨다")
    void shouldThrowExceptionWhenResumeDoesNotExist() {
        Long resumeId = 1L;
        Long memberId = 1L;
        EvaluationDto evaluationDto = new EvaluationDto();

        Mockito.when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> evaluationService.evaluate(resumeId, evaluationDto, memberId));
    }


    @Test
    @DisplayName("평가를 수정한다")
    void shouldUpdateEvaluationSuccessfully() {
        Long evaluationId = 1L;
        Long memberId = 1L;
        Evaluation evaluation = new Evaluation();
        Member member = new Member();
        EvaluationDto evaluationDto = new EvaluationDto();
        evaluationDto.setScore(8.0);
        evaluationDto.setComment("Well done!");

        Mockito.when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(evaluation));
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> evaluationService.update(evaluationId, evaluationDto, memberId));
    }

    @Test
    @DisplayName("존재하지 않은 평가를 업데이트 하면 오류를 발생시킨다")
    void shouldThrowExceptionWhenUpdatingNonExistentEvaluation() {
        Long evaluationId = 1L;
        Long memberId = 1L;
        EvaluationDto evaluationDto = new EvaluationDto();

        Mockito.when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.empty());
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(new Member()));

        assertDoesNotThrow(() -> evaluationService.update(evaluationId, evaluationDto, memberId));
    }

    @Test
    @DisplayName("평가를 조회한다")
    void shouldGetEvaluationSuccessfully() {
        Long evaluationId = 1L;
        Evaluation evaluation = new Evaluation();

        Mockito.when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(evaluation));

        assertDoesNotThrow(() -> evaluationService.getEvaluation(evaluationId));
    }

    @Test
    @DisplayName("존재하지 않는 평가를 조회하면 오류를 발생시킨다")
    void shouldThrowExceptionWhenGettingNonExistentEvaluation() {
        Long evaluationId = 1L;

        Mockito.when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> evaluationService.getEvaluation(evaluationId));
    }

    @Test
    @DisplayName("평가를 삭제한다")
    void shouldDeleteEvaluationSuccessfully() {
        Long evaluationId = 1L;

        // 실행
        evaluationService.deleteEvaluation(evaluationId);

        // 검증
        verify(evaluationRepository, times(1)).deleteById(evaluationId);
    }
}