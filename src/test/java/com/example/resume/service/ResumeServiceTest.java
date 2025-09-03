package com.example.resume.service;

import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.dto.ResumeResponseDto;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.cv.service.ResumeService;
import com.example.resume.cv.service.support.ResumeViewManager;
import com.example.resume.enums.CareerLevel;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.user.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @InjectMocks
    private ResumeService resumeService;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ValueOperations<String, Long> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ResumeViewManager resumeViewManager;

    private Member testMember;
    private Resume testResume;
    private List<Evaluation> evaluations;

    private final String local = "127.0.0.1";
    private final Clock clock = Clock.systemUTC();


    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .email("test@test.com")
                .name("테스트")
                .careerLevel(CareerLevel.JUNIOR)
                .role(Member.Role.USER)
                .build();

        evaluations = new ArrayList<>();
        evaluations.add(Evaluation.builder()
                        .id(1L)
                        .score(4.5)
                        .comment("좋은 이력서입니다.")
                        .evaluator(testMember)
                        .build());

        evaluations.add(Evaluation.builder()
                        .id(2L)
                        .score(4.0)
                        .evaluator(testMember)
                        .comment("인상적입니다.").build());

        testResume = Resume.builder()
                .id(1L)
                .member(testMember)
                .title("테스트 이력서")
                .fileUrl("/test/path")
                .keyword("Java, Spring")
                .build();
        testResume.setEvaluations(evaluations);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    @DisplayName("이력서 상세 조회 성공 테스트")
    void getResumeById() {
        // given
        given(resumeRepository.findByIdWithEvaluation(1L)).willReturn(Optional.of(testResume));
        // when
        ResumeResponseDto result = resumeService.getResumeById(1L, 1L, local);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 이력서");
        assertThat(result.getAverageScore()).isEqualTo(4.3);
        assertThat(result.getCommentSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("모든 이력서 조회 성공 테스트")
    void getAllResumesSuccess() {
        // given
        List<Resume> resumes = List.of(testResume);
        given(resumeRepository.findAllWithEvaluation()).willReturn(resumes);

        // when
        List<ResumeResponseDto> results = resumeService.getAllResumes();

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("테스트 이력서");
    }

    @Test
    @DisplayName("내 이력서 목록 조회 성공 테스트")
    void getMyResumesSuccess() {
        // given
        List<Resume> resumes = List.of(testResume);
        given(resumeRepository.findByMemberIdWithEvaluation(1L)).willReturn(resumes);

        // when
        List<ResumeResponseDto> results = resumeService.getMyResumes(1L);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("테스트 이력서");
    }

    @Test
    @DisplayName("이력서 삭제 - 성공")
    void deleteResume_Success() {
        //given
        given(resumeRepository.findById(1L)).willReturn(Optional.of(testResume));

        //then
        resumeService.deleteResume(testResume.getId());

        // then
        verify(evaluationRepository).deleteAllByResumeId(1L);
        verify(resumeRepository).deleteById(1L);
    }
}