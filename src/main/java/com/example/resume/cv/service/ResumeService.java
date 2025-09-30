package com.example.resume.cv.service;

import com.example.resume.common.annotation.LogExecutionTime;
import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.domain.ResumeInteraction;
import com.example.resume.cv.dto.ResumeRecentlyViewedResponseDto;
import com.example.resume.cv.dto.ResumeResponseDto;
import com.example.resume.cv.repository.jpa.ResumeInteractionRepository;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.cv.repository.queryDSL.ResumeInteractionQueryDSLRepository;
import com.example.resume.cv.repository.queryDSL.ResumeQueryDSLRepository;
import com.example.resume.cv.support.ResumeViewManager;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.evaluation.dto.EvaluationSummaryResponseDto;
import com.example.resume.evaluation.repository.EvaluationRepository;
import com.example.resume.openAI.service.OpenAIService;
import com.example.resume.user.domain.Member;
import com.example.resume.user.dto.MemberDto;
import com.example.resume.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResumeService {

    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final OpenAIService openAIService;
    private final ResumeViewManager resumeViewManager;
    private final EvaluationRepository evaluationRepository;
    private final ResumeQueryDSLRepository resumeQueryDSLRepository;
    private final ResumeInteractionRepository resumeInteractionRepository;
    private final ResumeInteractionQueryDSLRepository resumeInteractionQueryDSLRepository;

    private static final String UPLOAD_DIR = "/home/ec2-user/uploads/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @LogExecutionTime
    public List<ResumeResponseDto> getAllResumesContainingTitle(String title, Long memberId) {
        List<Resume> resumesWithEvaluation = resumeRepository.findAllWithEvaluationContainingTitle(title);

        List<ResumeInteraction> resumeInteractions = resumeInteractionQueryDSLRepository.getResumeInteractions(memberId);
        Map<Long, Boolean> resumeInteractionMap = new HashMap<>();
        for (ResumeInteraction resumeInteraction : resumeInteractions) {
            Boolean isEvaluated = resumeInteraction.getIsEvaluated();
            Long id = resumeInteraction.getResume().getId();
            resumeInteractionMap.put(id, isEvaluated);
        }
        Set<Long> keys = resumeInteractionMap.keySet();

        List<ResumeResponseDto> resumeResponseDtos = getResumeResponseDtos(resumesWithEvaluation);

        for (ResumeResponseDto resumeResponseDto : resumeResponseDtos) {
            Long resumeId = resumeResponseDto.getId();
            if (keys.contains(resumeId)){
                resumeResponseDto.setIsViewed(true);
                resumeResponseDto.setIsEvaluated(resumeInteractionMap.get(resumeId));
            }
        }
        return resumeResponseDtos;
    }



    @CacheEvict(value = "resumeList", allEntries = true)
    @LogExecutionTime
    @Transactional
    public void uploadFile(Long memberId, MultipartFile file, String title, String comment, Boolean isMailSent) throws IOException {
        Member member = findMemberById(memberId);
        String originalFileName = file.getOriginalFilename();
        byte[] fileBytes = file.getBytes();
        String fileUrl = saveFile(originalFileName, fileBytes);
        Resume resume = Resume.builder()
                .member(member)
                .title(title)
                .comment(comment)
                .fileUrl(fileUrl)
                .isMailSent(isMailSent)
                .build();
        resumeRepository.save(resume);
    }

    @Transactional(readOnly = true)
    @CacheEvict(value = "resumeList", allEntries = true)
    public ResumeResponseDto getResumeById(Long resumeId, Long memberId, String clientIp) {
        Member member = findMemberById(memberId);
        Resume resume = findByIdWithEvaluation(resumeId);
        Long resumeOwnerId = resume.getMember().getId();

        ResumeInteraction resumeInteraction = resumeInteractionQueryDSLRepository.getResumeInteraction(resumeId, memberId);
        if (resumeInteraction != null && !resumeOwnerId.equals(memberId)) {
            ResumeInteraction savedResumeInteraction = ResumeInteraction.builder()
                    .member(member)
                    .resume(resume)
                    .build();
            resumeInteractionRepository.save(savedResumeInteraction);
        }

        resumeViewManager.processViewCount(resumeId, memberId, clientIp);
        resumeViewManager.markViewed(memberId, resumeId, Instant.now());
        return buildResumeResponseDto(resume);
    }

    @Cacheable(value = "resumeList")
    @Transactional(readOnly = true)
    public List<ResumeResponseDto> getAllResumes(Long memberId) {
        List<Resume> resumesWithEvaluation = resumeRepository.findAllWithEvaluation();
        if (memberId != 0L){
            List<Long> resumeIds = resumesWithEvaluation.stream()
                    .map(Resume::getId)
                    .toList();
            List<ResumeInteraction> resumeInteractions = resumeInteractionQueryDSLRepository.getResumeInteractions(memberId);
        }
        return getResumeResponseDtos(resumesWithEvaluation);
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
        return getResumeResponseDtos(resumes);
    }

    @Transactional
    @CacheEvict(value = "resumeList", allEntries = true)
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서가 존재하지 않습니다 == " + resumeId));
        deleteFile(resume);
        evaluationRepository.deleteAllByResumeId(resumeId);;
        resumeRepository.deleteById(resumeId);
    }

    public List<ResumeRecentlyViewedResponseDto> getRecentlyViewedResumes(Long memberId) {
        List<Long> recentIds = resumeViewManager.getRecentIds(memberId, 5);
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < recentIds.size(); i++) {
            order.put(recentIds.get(i), i);
        }
        List<ResumeRecentlyViewedResponseDto> recentlyViewedResumes = resumeQueryDSLRepository.getRecentlyViewedResumes(recentIds);
        recentlyViewedResumes.sort(Comparator.comparingInt(r -> order.getOrDefault(r.resumeId(), Integer.MAX_VALUE)));
        return recentlyViewedResumes;
    }

    /*******private method*******/
    @NotNull
    private List<Long> getResumeIdsBy(List<ResumeInteraction> resumeInteractions) {
        return resumeInteractions.stream()
                .map(ResumeInteraction::getResume)
                .map(Resume::getId)
                .toList();
    }

    private Resume findByIdWithEvaluation(Long resumeId) {
        return resumeRepository.findByIdWithEvaluation(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다. resumeId: " + resumeId));
    }

    private Member findMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));
    }

    private String saveFile(String originalFileName, byte[] decodedBytes) {
        try {
            String datePath = LocalDate.now().format(DATE_FORMATTER);
            String fileName = generateUniqueFileName(originalFileName);
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

    private void deleteFile(Resume resume) {
        String path = resume.getFileUrl();
        File file = new File(path);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new RuntimeException("파일 삭제 실패: " + path);
            }
        }
    }

    private ResumeResponseDto buildResumeResponseDto(Resume resume) {
        List<Evaluation> evaluations = resume.getEvaluations();
        List<EvaluationSummaryResponseDto> evaluationDtos = evaluations.stream()
                .map(EvaluationSummaryResponseDto::fromEntity)
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
                resume.getViewCount(),
                resume.getComment()
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

    private List<ResumeResponseDto> getResumeResponseDtos(List<Resume> resumesWithEvaluation) {
        return resumesWithEvaluation.stream()
                .map(this::buildResumeResponseDto)
                .toList();
    }
}