package com.example.resume.resume.service;

import static com.example.resume.config.RedisConfig.RESUME_VIEW_COUNT_PREFIX;

import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.resume.resume.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeSyncService {
    private final ResumeRepository resumeRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    // 1분마다 작업을 수행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void syncFeedViewsToDb() {
        log.info("Starting syncFeedViewsToDb");
        Set<String> keys = redisTemplate.keys(RESUME_VIEW_COUNT_PREFIX + "*");
        if (keys.isEmpty()) {
            return;
        }

        keys.forEach(redisKey -> {
            Long resumeId = Long.parseLong(redisKey.replace(RESUME_VIEW_COUNT_PREFIX, ""));
            long redisViewCount = Optional.ofNullable(redisTemplate.opsForValue().get(redisKey))
                    .orElse(0L);
            if (redisViewCount > 0) {
                syncViewCount(redisKey, resumeId, redisViewCount);
            }
        });
        log.info("Finished syncFeedViewsToDb");
    }

    private void syncViewCount(String redisKey, Long resumeId, long redisViewCount) {
        log.info("starting Update");
        resumeRepository.incrementViewCount(resumeId, redisViewCount); // DB에 조회수 증가
        redisTemplate.delete(redisKey); // Redis에서 해당 키 삭제
    }
}
