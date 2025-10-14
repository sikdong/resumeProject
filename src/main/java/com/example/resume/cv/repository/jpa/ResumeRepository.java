package com.example.resume.cv.repository.jpa;

import com.example.resume.cv.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.evaluations LEFT JOIN FETCH r.member WHERE r.id = :resumeId")
    Optional<Resume> findByIdWithEvaluation(@Param("resumeId") Long resumeId);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.evaluations LEFT JOIN FETCH r.member ORDER BY r.createdAt desc")
    List<Resume> findAllWithEvaluation();

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.evaluations LEFT JOIN FETCH r.member WHERE r.member.id = :memberId ORDER BY r.createdAt desc")
    List<Resume> findByMemberIdWithEvaluation(@Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE Resume r SET r.viewCount = r.viewCount+:viewCount where r.id = :resumeId")
    void incrementViewCount(@Param("resumeId") Long resumeId, @Param("viewCount") Object viewCount);

    @Query("SELECT r FROM Resume r " +
            "LEFT JOIN FETCH r.evaluations " +
            "LEFT JOIN FETCH r.member " +
            "WHERE r.id in :ids " +
            "ORDER BY r.createdAt desc")
    List<Resume> findAllWithEvaluationByIdIn(@Param("ids") List<Long> ids);
}
