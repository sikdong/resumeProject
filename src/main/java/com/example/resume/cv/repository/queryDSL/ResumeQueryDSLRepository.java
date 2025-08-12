package com.example.resume.cv.repository.queryDSL;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ResumeQueryDSLRepository {
    private final JPAQueryFactory queryFactory;


}
