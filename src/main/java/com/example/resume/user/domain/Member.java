package com.example.resume.user.domain;

import com.example.resume.enums.CareerLevel;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.cv.domain.Resume;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oauthId;

    private String provider;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private CareerLevel careerLevel;

    @Column(name = "job_title")
    private String jobTitle;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Resume> resumes;

    @OneToMany(mappedBy = "evaluator")
    private List<Evaluation> evaluations;

    public enum Role {
        USER, ADMIN
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
