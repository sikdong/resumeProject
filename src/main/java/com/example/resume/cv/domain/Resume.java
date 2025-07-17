package com.example.resume.cv.domain;
import com.example.resume.evaluation.domain.Evaluation;
import com.example.resume.user.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

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
    private String keyword;

    @Column(name = "view_count", columnDefinition = "bigint default 0")
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "resume", fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<Evaluation> evaluations;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

