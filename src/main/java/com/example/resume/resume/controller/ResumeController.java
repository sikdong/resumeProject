package com.example.resume.resume.controller;

import com.example.resume.resume.dto.ResumeResponseDto;
import com.example.resume.resume.dto.ResumeUploadRequestDto;
import com.example.resume.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        return ResponseEntity.ok("파일 업로드 성공");
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponseDto> getResume (@PathVariable Long resumeId) {
        ResumeResponseDto resumeResponseDto = resumeService.getResumeById(resumeId);
        return ResponseEntity.ok(resumeResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponseDto>> getAllResumes () {
        List<ResumeResponseDto> resumeResponseDtos = resumeService.getAllResumes();
        return ResponseEntity.ok(resumeResponseDtos);
    }
}
