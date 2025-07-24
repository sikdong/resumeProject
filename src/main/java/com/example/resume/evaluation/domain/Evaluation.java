package com.example.resume.evaluation.domain;

import com.example.resume.user.domain.Member;
import com.example.resume.cv.domain.Resume;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Evaluation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double score;

    @Lob
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member evaluator;

    @UpdateTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;
}

