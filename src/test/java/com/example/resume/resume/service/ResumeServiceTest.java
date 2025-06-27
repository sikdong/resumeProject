package com.example.resume.resume.service;

import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.openAI.service.OpenAIService;
import com.example.resume.resume.domain.Resume;
import com.example.resume.resume.dto.ResumeResponseDto;
import com.example.resume.resume.dto.ResumeUploadRequestDto;
import com.example.resume.resume.repository.ResumeRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @InjectMocks
    private ResumeService resumeService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ValueOperations<String, Long> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    private Member testMember;
    private Resume testResume;
    private List<Evaluation> evaluations;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .email("test@test.com")
                .name("테스트")
                .build();

        evaluations = new ArrayList<>();
        evaluations.add(Evaluation.builder().score(4.5).comment("좋은 이력서입니다.").build());
        evaluations.add(Evaluation.builder().score(4.0).comment("인상적입니다.").build());

        testResume = Resume.builder()
                .id(1L)
                .member(testMember)
                .title("테스트 이력서")
                .fileUrl("/test/path")
                .keyword("Java, Spring")
                .build();
        testResume.setEvaluations(evaluations);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    @DisplayName("이력서 업로드 성공 테스트")
    void uploadResumeSuccess() throws IOException {
        // given
        ResumeUploadRequestDto requestDto = new ResumeUploadRequestDto("테스트 이력서", "test.pdf", "test");
        String content = "테스트 내용";
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
        given(openAIService.getResumeKeyword(any())).willReturn("Java, Spring");
        given(resumeRepository.save(any())).willReturn(testResume);

        // when
        resumeService.uploadResume(1L, requestDto, content);

        // then
        verify(resumeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 이력서 업로드 시 예외 발생")
    void uploadResumeWithInvalidMember() {
        // given
        ResumeUploadRequestDto requestDto = new ResumeUploadRequestDto("테스트 이력서", "test.pdf", "test");
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> resumeService.uploadResume(999L, requestDto, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("이력서 상세 조회 성공 테스트")
    void getResumeByIdSuccess() {
        // given
        given(resumeRepository.findByIdWithEvaluation(1L)).willReturn(Optional.of(testResume));
        given(hashOperations.hasKey(any(), any())).willReturn(false);

        // when
        ResumeResponseDto result = resumeService.getResumeById(1L, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 이력서");
        assertThat(result.getAverageScore()).isEqualTo(4.25);
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
}