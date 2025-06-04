package com.example.resume.resume.repository;

import com.example.resume.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByMemberId(Long userId);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.evaluations WHERE r.id = :resumeId")
    Optional<Resume> findByIdWithEvaluation(@Param("resumeId") Long resumeId);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.evaluations ORDER BY r.createdAt desc")
    List<Resume> findAllWithEvaluation();
}
