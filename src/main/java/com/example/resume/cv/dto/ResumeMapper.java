package com.example.resume.cv.dto;

import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.domain.ResumeDocument;
import org.springframework.stereotype.Component;

@Component
public class ResumeMapper {
    public ResumeDocument toDocument(Resume entity) {
        ResumeDocument doc = new ResumeDocument();
        doc.setId(entity.getId().toString());
        doc.setKeyword(entity.getKeyword());
        return doc;
    }
}