package com.example.resume.cv.domain;

import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.user.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@DynamicInsert
public class Resume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String fileUrl;

    @Column(columnDefinition = "varchar(255) default ''")
    private String comment;

    @Column(columnDefinition = "varchar(255) default ''")
    private String keyword;

    @Column(name = "view_count", columnDefinition = "bigint default 0")
    private Long viewCount;
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "resume", fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<Evaluation> evaluations;
}

