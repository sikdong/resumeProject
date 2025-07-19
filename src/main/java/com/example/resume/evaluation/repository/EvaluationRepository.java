package com.example.resume.evaluation.repository;

import com.example.resume.evaluation.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByResumeId(Long resumeId);

    @Modifying
    @Query("Delete FROM Evaluation e WHERE e.resume.id = :resumeId")
    void deleteAllByResumeId(Long resumeId);

    @Query("SELECT e FROM Evaluation e LEFT JOIN FETCH e.resume WHERE e.evaluator.id = :memberId")
    List<Evaluation> findByMemberIdWithResume(Long memberId);
}
