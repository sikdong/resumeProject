package com.example.resume.cv.repository.queryDSL;

import com.example.resume.cv.domain.ResumeInteraction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.resume.cv.domain.QResumeInteraction.resumeInteraction;

@Repository
@RequiredArgsConstructor
public class ResumeInteractionQueryDSLRepository {
    private final JPAQueryFactory queryFactory;

    public List<ResumeInteraction> getResumeInteractions(Long memberId){
        return queryFactory
                .selectFrom(resumeInteraction)
                .where(resumeInteraction.member.id.eq(memberId))
                .fetch();
    }

    public ResumeInteraction getResumeInteraction(Long memberId, Long resumeId){
        return queryFactory
                .selectFrom(resumeInteraction)
                .where(resumeInteraction.member.id.eq(memberId)
                        .and(resumeInteraction.resume.id.eq(resumeId)))
                .fetchOne();
    }
}
