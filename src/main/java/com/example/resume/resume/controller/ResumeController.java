package com.example.resume.resume.controller;

import com.example.resume.resume.dto.ResumeResponseDto;
import com.example.resume.resume.dto.ResumeUploadRequestDto;
import com.example.resume.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @GetMapping("pdf/{fileName}")
    public ResponseEntity<Resource> getPdf(@PathVariable String fileName) throws MalformedURLException {
        Path filePath = Paths.get("src/main/resources/static/uploads").resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .headers(headers)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
