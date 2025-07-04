package com.example.resume.cv.controller;

import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.domain.ResumeDocument;
import com.example.resume.cv.dto.ResumeResponseDto;
import com.example.resume.cv.search.ResumeSearchRepository;
import com.example.resume.cv.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resume-search")
@RequiredArgsConstructor
public class ResumeSearchController {
    private final ResumeSearchRepository resumeSearchRepository;
    private final ResumeService resumeService;

    @GetMapping("/keyword")
    public List<ResumeResponseDto> search(@RequestParam String keyword) {
        List<ResumeDocument> resumeDocuments = resumeSearchRepository.findByKeywordContaining(keyword);
        List<Long> ids = resumeDocuments.stream()
                .map(ResumeDocument::getId)
                .map(Long::valueOf)
                .toList();
        return resumeService.getResumesByIds(ids);
    }

}
