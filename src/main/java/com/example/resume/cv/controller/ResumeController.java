package com.example.resume.cv.controller;

import com.example.resume.common.MemberUtil;
import com.example.resume.cv.dto.ResumeRecentlyViewedResponseDto;
import com.example.resume.cv.dto.ResumeResponseDto;
import com.example.resume.cv.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @GetMapping
    public ResponseEntity<List<ResumeResponseDto>> listResumes(@RequestParam(required = false) String title,
                                                               Authentication authentication) {
        Long memberId = MemberUtil.getMemberId(authentication);
        if (StringUtils.hasText(title)) {
            return ResponseEntity.ok(resumeService.getAllResumesContainingTitle(title.trim(), memberId));
        }
        return ResponseEntity.ok(resumeService.getAllResumes(memberId));
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponseDto> getResume (@PathVariable Long resumeId,
                                                        Authentication authentication,
                                                        HttpServletRequest request) {
        Long memberId = MemberUtil.getMemberId(authentication);
        String clientIp = MemberUtil.getClientIp(request);
        ResumeResponseDto resumeResponseDto = resumeService.getResumeById(resumeId, memberId, clientIp);
        return ResponseEntity.ok(resumeResponseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ResumeResponseDto>> getMyResumes (Authentication authentication) {
        Long memberId = MemberUtil.getMemberId(authentication);
        List<ResumeResponseDto> resumeResponseDtos = resumeService.getMyResumes(memberId);
        return ResponseEntity.ok(resumeResponseDtos);
    }

    @GetMapping("/{resumeId}/file")
    public ResponseEntity<Resource> getFile(@PathVariable Long resumeId) throws MalformedURLException {
        Path filePath = resumeService.getFilePath(resumeId);
        String fileName = filePath.getFileName().toString();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename*=UTF-8''" + encodedFileName);
            headers.add("X-Frame-Options", "ALLOWALL");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .headers(headers)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam("isMailSent") Boolean isMailSent,
            Authentication authentication
    ) throws IOException {
        Long memberId = MemberUtil.getMemberId(authentication);
        resumeService.uploadFile(memberId, file, title, comment, isMailSent);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long resumeId){
        resumeService.deleteResume(resumeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/recently-viewed")
    public ResponseEntity<List<ResumeRecentlyViewedResponseDto>> getRecentlyViewedResumes(Authentication authentication){
        Long memberId = MemberUtil.getMemberId(authentication);
        List<ResumeRecentlyViewedResponseDto> recentlyViewedResumes = resumeService.getRecentlyViewedResumes(memberId);
        return ResponseEntity.ok(recentlyViewedResumes);
    }
}
