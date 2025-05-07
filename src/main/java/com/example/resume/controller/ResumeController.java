package com.example.resume.controller;

import com.example.resume.domain.Resume;
import com.example.resume.dto.ResumeResponseDto;
import com.example.resume.dto.ResumeUploadRequestDto;
import com.example.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping("/upload/{userId}")
    public ResponseEntity<String> uploadResume (
            @PathVariable Long userId,
            @RequestBody ResumeUploadRequestDto request
            ){
        String content = request.content().split(",")[1];
        byte[] decodedContent = Base64.getDecoder().decode(content);
        resumeService.uploadResume(userId, request, decodedContent);
        System.out.println();
        return ResponseEntity.ok("파일 업로드 성공");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeResponseDto>> getResumesByUser(@PathVariable Long userId) {
        List<ResumeResponseDto> resumes = resumeService.getResumesByUser(userId);
        return ResponseEntity.ok(resumes);
    }

    @PutMapping("/{resumeId}/evaluate")
    public ResponseEntity<Resume> evaluateResume(
            @PathVariable Long resumeId,
            @RequestParam int score
    ) {
        Resume evaluated = resumeService.evaluateResume(resumeId, score);
        return ResponseEntity.ok(evaluated);
    }
}
