package com.example.resume.cv.repository.queryDSL;

import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.dto.ResumeRecentlyViewedResponseDto;
import com.example.resume.evaluation.domain.QEvaluation;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.resume.cv.domain.QResume.resume;
import static com.example.resume.evaluation.domain.QEvaluation.evaluation;
import static com.example.resume.user.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class ResumeQueryDSLRepository {
    private final JPAQueryFactory queryFactory;

    public List<ResumeRecentlyViewedResponseDto> getRecentlyViewedResumes(List<Long> ids){
            return queryFactory
                    .select(Projections.constructor(ResumeRecentlyViewedResponseDto.class,
                            resume.id,
                            resume.title
                    ))
                    .from(resume)
                    .where(resume.id.in(ids))
                    .fetch();
    }

    public List<Resume> findAllWithEvaluationBy(String searchValue){
        return queryFactory
                .selectFrom(resume)
                .leftJoin(resume.evaluations, evaluation)
                .fetchJoin()
                .leftJoin(resume.member, member)
                .fetchJoin()
                .where(
                        searchResumes(searchValue)
                )
                .orderBy(resume.createdAt.desc())
                .fetch();
    }

    private BooleanExpression titleContains(String searchValue){
        return searchValue == null ? null : resume.title.containsIgnoreCase(searchValue);
    }

    private BooleanExpression keywordContains(String searchValue){
        return searchValue == null ? null : resume.keyword.containsIgnoreCase(searchValue);
    }

    private BooleanExpression searchResumes(String searchValue){
        BooleanExpression titleContains = titleContains(searchValue);
        BooleanExpression keywordContains = keywordContains(searchValue);
        if (searchValue != null){
            return titleContains.or(keywordContains);
        }
        return null;
    }
}
