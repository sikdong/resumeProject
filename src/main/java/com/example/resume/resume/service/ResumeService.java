package com.example.resume.resume.service;

import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.evaluation.dto.EvaluationResponseDto;
import com.example.resume.openAI.service.OpenAIService;
import com.example.resume.resume.domain.Resume;
import com.example.resume.resume.dto.ResumeResponseDto;
import com.example.resume.resume.dto.ResumeUploadRequestDto;
import com.example.resume.resume.repository.ResumeRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.dto.MemberDto;
import com.example.resume.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResumeService {

    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final OpenAIService openAIService;

    private static final String UPLOAD_DIR = "/uploads/";

    public void uploadResume(Long userId, ResumeUploadRequestDto request, String content) throws IOException {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        byte[] decodedContent = Base64.getDecoder().decode(content);
        String fileUrl = saveFile(request, decodedContent);
        log.info("fileUrl === {}", fileUrl);
        String keyword = openAIService.getResumeKeyword(content);
        Resume resume = Resume.builder()
                .member(member)
                .title(request.title())
                .fileUrl(fileUrl)
                .keyword(keyword)
                .build();
        resumeRepository.save(resume);
    }

    private String saveFile(ResumeUploadRequestDto request, byte[] decodedBytes) {
        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID() + "_" + request.fileName();
            Path path = Paths.get(UPLOAD_DIR, datePath, fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, decodedBytes);
            return path.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }
    @Transactional(readOnly = true)
    public ResumeResponseDto getResumeById(Long resumeId) {
        Resume resume = resumeRepository.findByIdWithEvaluation(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("no resume"));

        List<Evaluation> evaluations = resume.getEvaluations();
        return getResumeResponseDto(evaluations, resume);
    }

    public List<ResumeResponseDto> getAllResumes() {
        List<Resume> resumesWithEvaluation = resumeRepository.findAllWithEvaluation();
        return getResumeResponseDtos(resumesWithEvaluation);
    }

    public Path getFilePath(Long fileId) {
        Resume resume = resumeRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("no resume"));
        String fileUrl = resume.getFileUrl();
        return Paths.get(fileUrl);
    }
    private ResumeResponseDto getResumeResponseDto(List<Evaluation> evaluations, Resume resume) {
        List<EvaluationResponseDto> evaluationDtos = evaluations.stream()
                .map(EvaluationResponseDto::fromEntity)
                .toList();

        double averageScore = getAverageScore(evaluations);
        int commentSize = getCommentSize(evaluations);

        return new ResumeResponseDto(
                resume.getId(),
                resume.getTitle(),
                resume.getFileUrl(),
                resume.getKeyword(),
                resume.getCreatedAt(),
                averageScore,
                commentSize,
                evaluationDtos,
                MemberDto.fromEntity(resume.getMember())
        );
    }

    private List<ResumeResponseDto> getResumeResponseDtos(List<Resume> resumes) {
        List<ResumeResponseDto> resumeResponseDtos = new ArrayList<>();
        for (Resume resume : resumes) {
            List<Evaluation> evaluations = resume.getEvaluations();
            ResumeResponseDto resumeResponseDto = getResumeResponseDto(evaluations, resume);
            resumeResponseDtos.add(resumeResponseDto);
        }
        return resumeResponseDtos;
    }

    private int getCommentSize(List<Evaluation> evaluations) {
        return (int) evaluations.stream()
                .filter(e -> e.getComment() != null && !e.getComment().isBlank())
                .count();
    }

    private double getAverageScore(List<Evaluation> evaluations) {
        double average = evaluations.stream()
                .mapToDouble(Evaluation::getScore)
                .average()
                .orElse(0.0);
        return Math.round(average * 10) / 10.0;
    }
}