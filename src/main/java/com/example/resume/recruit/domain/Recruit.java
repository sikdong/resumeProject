package com.example.resume.recruit.domain;

import com.example.resume.cv.domain.Resume;
import com.example.resume.user.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Recruit {
    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "varchar(255) default ''")
    private String companyName;
    @Column(columnDefinition = "varchar(255) default ''")
    private String recruitUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
