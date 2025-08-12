package com.example.resume.recruit.repository;

import com.example.resume.recruit.domain.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
}
