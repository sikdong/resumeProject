package com.example.resume.cv.search;

import com.example.resume.cv.domain.ResumeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ResumeSearchRepository extends ElasticsearchRepository<ResumeDocument, String> {
    List<ResumeDocument> findByKeywordContaining(String keyword);
}
