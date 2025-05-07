package com.example.resume.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Evaluation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    @Lob
    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne
    @JoinColumn(name = "evaluator_id")
    private User evaluator;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

