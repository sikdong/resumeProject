package com.example.resume.resume.service;

import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.evaluation.dto.EvaluationResponseDto;
import com.example.resume.openAI.service.OpenAIService;
import com.example.resume.resume.domain.Resume;
import com.example.resume.resume.dto.ResumeResponseDto;
import com.example.resume.resume.dto.ResumeUploadRequestDto;
import com.example.resume.resume.repository.ResumeRepository;
import com.example.resume.resume.service.support.ResumeViewManager;
import com.example.resume.user.domain.Member;
import com.example.resume.user.dto.MemberDto;
import com.example.resume.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.example.resume.config.RedisConfig.RESUME_VIEWED_MEMBER_DAY;
import static com.example.resume.config.RedisConfig.RESUME_VIEWED_NOT_MEMBER;
import static com.example.resume.config.RedisConfig.RESUME_VIEW_COUNT_PREFIX;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResumeService {

    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final OpenAIService openAIService;
    private final ResumeViewManager resumeViewManager;

    private static final String UPLOAD_DIR = "/home/ec2-user/uploads/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @CacheEvict(value = "resumeList", allEntries = true)
    @Transactional
    public void uploadResume(Long userId, ResumeUploadRequestDto request, String content) throws IOException {
        Member member = findMemberById(userId);
        byte[] decodedContent = decodeBase64Content(content);
        String fileUrl = saveFile(request, decodedContent);
        log.info("파일이 성공적으로 저장되었습니다. fileUrl: {}", fileUrl);
        
        String keyword = openAIService.getResumeKeyword(content);
        Resume resume = Resume.builder()
                .member(member)
                .title(request.title())
                .fileUrl(fileUrl)
                .keyword(keyword)
                .build();
        resumeRepository.save(resume);
    }

    private Member findMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));
    }

    private byte[] decodeBase64Content(String content) {
        try {
            return java.util.Base64.getDecoder().decode(content);
        } catch (IllegalArgumentException e) {
            log.error("Base64 디코딩에 실패했습니다.", e);
            throw new IllegalArgumentException("잘못된 파일 형식입니다.", e);
        }
    }

    private String saveFile(ResumeUploadRequestDto request, byte[] decodedBytes) {
        try {
            String datePath = LocalDate.now().format(DATE_FORMATTER);
            String fileName = generateUniqueFileName(request.fileName());
            Path filePath = Paths.get(UPLOAD_DIR, datePath, fileName);
            
            createDirectoriesIfNotExists(filePath);
            Files.write(filePath, decodedBytes);
            
            return filePath.toString();
        } catch (IOException e) {
            log.error("파일 저장 중 오류가 발생했습니다.", e);
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID() + "_" + originalFileName;
    }

    private void createDirectoriesIfNotExists(Path filePath) throws IOException {
        Path parentDir = filePath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    @Transactional(readOnly = true)
    @CacheEvict(value = "resumeList", allEntries = true)
    public ResumeResponseDto getResumeById(Long resumeId, Long memberId, String clientIp) {
        Resume resume = findResumeByIdWithEvaluation(resumeId);
        resumeViewManager.processViewCount(resumeId, memberId, clientIp);
        return buildResumeResponseDto(resume);
    }

    private Resume findResumeByIdWithEvaluation(Long resumeId) {
        return resumeRepository.findByIdWithEvaluation(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다. resumeId: " + resumeId));
    }

    @Cacheable(value = "resumeList")
    @Transactional(readOnly = true)
    public List<ResumeResponseDto> getAllResumes() {
        List<Resume> resumesWithEvaluation = resumeRepository.findAllWithEvaluation();
        return resumesWithEvaluation.stream()
                .map(this::buildResumeResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Path getFilePath(Long fileId) {
        Resume resume = resumeRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다. fileId: " + fileId));
        return Paths.get(resume.getFileUrl());
    }

    @Transactional(readOnly = true)
    public List<ResumeResponseDto> getMyResumes(Long memberId) {
        List<Resume> resumes = resumeRepository.findByMemberIdWithEvaluation(memberId);
        return resumes.stream()
                .map(this::buildResumeResponseDto)
                .toList();
    }

    private ResumeResponseDto buildResumeResponseDto(Resume resume) {
        List<Evaluation> evaluations = resume.getEvaluations();
        List<EvaluationResponseDto> evaluationDtos = evaluations.stream()
                .map(EvaluationResponseDto::fromEntity)
                .toList();

        double averageScore = calculateAverageScore(evaluations);
        int commentCount = countComments(evaluations);

        return new ResumeResponseDto(
                resume.getId(),
                resume.getTitle(),
                resume.getFileUrl(),
                resume.getKeyword(),
                resume.getCreatedAt(),
                averageScore,
                commentCount,
                evaluationDtos,
                MemberDto.fromEntity(resume.getMember()),
                resume.getViewCount()
        );
    }

    private int countComments(List<Evaluation> evaluations) {
        return (int) evaluations.stream()
                .filter(this::hasValidComment)
                .count();
    }

    private boolean hasValidComment(Evaluation evaluation) {
        return evaluation.getComment() != null && !evaluation.getComment().isBlank();
    }

    private double calculateAverageScore(List<Evaluation> evaluations) {
        if (evaluations.isEmpty()) {
            return 0.0;
        }
        
        double average = evaluations.stream()
                .mapToDouble(Evaluation::getScore)
                .average()
                .orElse(0.0);
        
        return Math.round(average * 10) / 10.0;
    }
}