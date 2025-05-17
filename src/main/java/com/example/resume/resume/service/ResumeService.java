package com.example.resume.resume.service;

import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.resume.domain.Resume;
import com.example.resume.user.domain.User;
import com.example.resume.evaluation.dto.EvaluationResponseDto;
import com.example.resume.resume.dto.ResumeResponseDto;
import com.example.resume.resume.dto.ResumeUploadRequestDto;
import com.example.resume.resume.repository.ResumeRepository;
import com.example.resume.user.repository.UserRepository;
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
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResumeService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    private static final String UPLOAD_DIR = "/uploads/";

    public void uploadResume(Long userId, ResumeUploadRequestDto request, byte[] decodedContent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        String fileUrl = saveFile(request, decodedContent);
        log.info("fileUrl === {}", fileUrl);
        Resume resume = Resume.builder()
                .user(user)
                .title(request.title())
                .fileUrl(fileUrl)
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
            log.error(e.getMessage());
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }
    @Transactional(readOnly = true)
    public ResumeResponseDto getResumeById(Long resumeId) {
        Resume resume = resumeRepository.findByIdWithEvaluation(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("no resume"));

        List<Evaluation> evaluations = resume.getEvaluations();

        List<EvaluationResponseDto> evaluationDtos = evaluations.stream()
                .map(EvaluationResponseDto::fromEntity)
                .toList();

        double averageScore = getAverageScore(evaluations);
        int commentSize = getCommentSize(evaluations);

        return new ResumeResponseDto(
                resumeId,
                resume.getTitle(),
                resume.getFileUrl(),
                resume.getCreatedAt(),
                averageScore,
                commentSize,
                evaluationDtos
        );
    }

    private int getCommentSize(List<Evaluation> evaluations) {
        return (int) evaluations.stream()
                .filter(e -> e.getComment() != null && !e.getComment().isBlank())
                .count();
    }

    private double getAverageScore(List<Evaluation> evaluations) {
        return evaluations.stream()
                .mapToDouble(Evaluation::getScore)
                .average()
                .orElse(0.0); // 평가가 없을 경우 평균 점수 0.0
    }
}
