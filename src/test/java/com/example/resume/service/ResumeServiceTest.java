package com.example.resume.service;

import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.domain.ResumeInteraction;
import com.example.resume.cv.dto.ResumeResponseDto;
import com.example.resume.cv.repository.jpa.ResumeInteractionRepository;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.cv.service.ResumeService;
import com.example.resume.cv.support.ResumeViewManager;
import com.example.resume.enums.CareerLevel;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.openAI.service.OpenAIService;
import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
import com.example.resume.cv.repository.queryDSL.ResumeInteractionQueryDSLRepository;
import com.example.resume.cv.repository.queryDSL.ResumeQueryDSLRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @InjectMocks
    private ResumeService resumeService;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private ResumeQueryDSLRepository resumeQueryDSLRepository;

    @Mock
    private ResumeInteractionRepository resumeInteractionRepository;

    @Mock
    private ResumeInteractionQueryDSLRepository resumeInteractionQueryDSLRepository;

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
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
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
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));

        // when
        List<ResumeResponseDto> results = resumeService.getAllResumes(testMember.getId());

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

    @Test
    @DisplayName("파일 업로드 성공 테스트")
    void uploadFile_Success() throws Exception {
        // given
        String originalFileName = "resume.pdf";
        String fileContent = "test content";
        byte[] fileBytes = fileContent.getBytes();
        String savedFilePath = "/path/to/saved/resume.pdf";

        given(multipartFile.getOriginalFilename()).willReturn(originalFileName);
        given(multipartFile.getBytes()).willReturn(fileBytes);
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
        given(resumeRepository.save(any(Resume.class))).willReturn(testResume);

        // when
        resumeService.uploadFile(1L, multipartFile, "Test Resume", "Great job", false);

        // then
        verify(memberRepository).findById(1L);
        verify(resumeRepository).save(any(Resume.class));
    }


    @Test
    @DisplayName("파일 업로드 실패 테스트 - 파일 저장 오류")
    void uploadFile_FileSaveFailure() throws Exception {
        // given
        String originalFileName = "resume.pdf";
        given(multipartFile.getOriginalFilename()).willReturn(originalFileName);
        given(multipartFile.getBytes()).willThrow(new IOException("파일 저장에 실패했습니다."));
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));

        // when & then
        Throwable exception = assertThrows(IOException.class,
                () -> resumeService.uploadFile(1L, multipartFile, "Test Resume", "Unable to save", false));

        assertThat(exception.getMessage()).isEqualTo("파일 저장에 실패했습니다.");
    }

    @Test
    @DisplayName("다른 사용자가 이력서를 열람하면 열람 및 평가 여부가 저장된다")
    void getResumeById_viewerNotOwner_recordsInteractionAndReturnsDto() {
        Member owner = buildMember(2L, "owner@test.com", "Owner", CareerLevel.JUNIOR, "Backend Engineer");
        Member viewer = buildMember(3L, "viewer@test.com", "Viewer", CareerLevel.SENIOR, "Reviewer");

        Resume resume = buildResume(100L, "Backend Resume", "/files/backend.pdf", "Java", 7L, "Focus on Spring", owner);
        resume.setEvaluations(List.of(
                buildEvaluation(10L, 4.0, "Solid background", resume, viewer),
                buildEvaluation(11L, 5.0, "Great side projects", resume, owner)
        ));

        ResumeInteraction interaction = ResumeInteraction.builder()
                .id(55L)
                .member(viewer)
                .resume(resume)
                .isEvaluated(Boolean.FALSE)
                .build();

        when(memberRepository.findById(viewer.getId())).thenReturn(Optional.of(viewer));
        when(resumeRepository.findByIdWithEvaluation(resume.getId())).thenReturn(Optional.of(resume));
        when(resumeInteractionQueryDSLRepository.getResumeInteraction(anyLong(), anyLong())).thenReturn(interaction);

        ResumeResponseDto response = resumeService.getResumeById(resume.getId(), viewer.getId(), "127.0.0.1");

        assertThat(response.getId()).isEqualTo(resume.getId());
        assertThat(response.getTitle()).isEqualTo(resume.getTitle());
        assertThat(response.getAverageScore()).isEqualTo(4.5);
        assertThat(response.getCommentSize()).isEqualTo(2);
        assertThat(response.getMember().id()).isEqualTo(owner.getId());

        verify(memberRepository).findById(viewer.getId());
        verify(resumeRepository).findByIdWithEvaluation(resume.getId());
        verify(resumeInteractionQueryDSLRepository).getResumeInteraction(anyLong(), anyLong());

        ArgumentCaptor<ResumeInteraction> interactionCaptor = ArgumentCaptor.forClass(ResumeInteraction.class);
        verify(resumeInteractionRepository).save(interactionCaptor.capture());
        ResumeInteraction savedInteraction = interactionCaptor.getValue();
        assertThat(savedInteraction.getMember()).isEqualTo(viewer);
        assertThat(savedInteraction.getResume()).isEqualTo(resume);

        verify(resumeViewManager).processViewCount(resume.getId(), viewer.getId(), "127.0.0.1");
        verify(resumeViewManager).markViewed(eq(viewer.getId()), eq(resume.getId()), any(Instant.class));
    }

    @Test
    @DisplayName("이력서 소유자가 조회할 때는 열람 및 평가 여부가 저장되지 않는다")
    void getResumeById_ownerView_doesNotPersistInteraction() {
        Resume resume = Resume.builder()
                .id(200L)
                .title("Owner Resume")
                .fileUrl("/files/owner.pdf")
                .keyword("Spring")
                .member(testMember)
                .build();

        resume.setEvaluations(evaluations);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(resumeRepository.findByIdWithEvaluation(resume.getId())).thenReturn(Optional.of(resume));
        when(resumeInteractionQueryDSLRepository.getResumeInteraction(anyLong(), anyLong())).thenReturn(null);

        ResumeResponseDto response = resumeService.getResumeById(resume.getId(), testMember.getId(), "127.0.0.1");

        assertThat(response.getMember().id()).isEqualTo(testMember.getId());

        verify(resumeInteractionRepository, never()).save(any());
        verify(resumeViewManager).processViewCount(resume.getId(), testMember.getId(), "127.0.0.1");
        verify(resumeViewManager).markViewed(eq(testMember.getId()), eq(resume.getId()), any(Instant.class));
    }

    private Member buildMember(Long id, String email, String name, CareerLevel level, String jobTitle) {
        return Member.builder()
                .id(id)
                .email(email)
                .name(name)
                .careerLevel(level)
                .role(Member.Role.USER)
                .jobTitle(jobTitle)
                .build();
    }

    private Resume buildResume(Long id, String title, String fileUrl, String keyword, Long viewCount, String comment, Member owner) {
        return Resume.builder()
                .id(id)
                .title(title)
                .fileUrl(fileUrl)
                .keyword(keyword)
                .viewCount(viewCount)
                .comment(comment)
                .member(owner)
                .build();
    }

    private Evaluation buildEvaluation(Long id, double score, String comment, Resume resume, Member evaluator) {
        return Evaluation.builder()
                .id(id)
                .score(score)
                .comment(comment)
                .resume(resume)
                .evaluator(evaluator)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
