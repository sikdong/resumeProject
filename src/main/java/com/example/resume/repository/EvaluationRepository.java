package com.example.resume.repository;

import com.example.resume.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByResumeId(Long resumeId);
}
