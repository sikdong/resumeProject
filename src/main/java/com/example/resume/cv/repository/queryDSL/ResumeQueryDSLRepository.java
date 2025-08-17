package com.example.resume.cv.repository.queryDSL;

import com.example.resume.cv.dto.ResumeRecentlyViewedResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.resume.cv.domain.QResume.resume;

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
}
