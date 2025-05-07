package com.example.resume.service;

import com.example.resume.domain.Resume;
import com.example.resume.domain.User;
import com.example.resume.dto.ResumeResponseDto;
import com.example.resume.dto.ResumeUploadRequestDto;
import com.example.resume.repository.ResumeRepository;
import com.example.resume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

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

    // 이력서 리스트 조회
    public List<ResumeResponseDto> getResumesByUser(Long userId) {
        return resumeRepository.findByUserId(userId)
                .stream()
                .map(ResumeResponseDto::fromEntity)
                .collect(toList());
    }

    // 이력서 평가 점수 등록
    public Resume evaluateResume(Long resumeId, int score) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        resume.setScore(score);
        return resumeRepository.save(resume);
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



}
