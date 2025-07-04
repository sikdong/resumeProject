package com.example.resume.cv.service;

import com.example.resume.cv.repository.jpa.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.example.resume.config.RedisConfig.RESUME_VIEWED_MEMBER_DAY;
import static com.example.resume.config.RedisConfig.RESUME_VIEWED_NOT_MEMBER;
import static com.example.resume.config.RedisConfig.RESUME_VIEW_COUNT_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeSyncService {
    private final ResumeRepository resumeRepository;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    // 1분마다 작업을 수행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void syncFeedViewsToDb() {
        Set<String> keys = objectRedisTemplate.keys(RESUME_VIEW_COUNT_PREFIX + "*");
        if (keys.isEmpty()) {
            return;
        }

        keys.forEach(redisKey -> {
            Long resumeId = Long.parseLong(redisKey.replace(RESUME_VIEW_COUNT_PREFIX, ""));
            Object redisViewCount = objectRedisTemplate.opsForValue().get(redisKey);
            if (redisViewCount != null) {
                syncViewCount(redisKey, resumeId, redisViewCount);
            }
        });
        log.info("Finished syncFeedViewsToDb");
    }

    private void syncViewCount(String redisKey, Long resumeId, Object redisViewCount) {
        log.info("starting Update");
        resumeRepository.incrementViewCount(resumeId, redisViewCount); // DB에 조회수 증가
        objectRedisTemplate.delete(redisKey); // Redis에서 해당 키 삭제
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearViewedMember(){
        objectRedisTemplate.delete(RESUME_VIEWED_MEMBER_DAY);
        objectRedisTemplate.delete(RESUME_VIEWED_NOT_MEMBER);
    }
}
