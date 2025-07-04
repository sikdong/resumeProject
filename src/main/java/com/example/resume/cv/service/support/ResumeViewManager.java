package com.example.resume.cv.service.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.example.resume.config.RedisConfig.RESUME_VIEWED_MEMBER_DAY;
import static com.example.resume.config.RedisConfig.RESUME_VIEWED_NOT_MEMBER;
import static com.example.resume.config.RedisConfig.RESUME_VIEW_COUNT_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeViewManager {

    private final RedisTemplate<String, Object> objectRedisTemplate;

    public void processViewCount(Long resumeId, Long memberId, String clientIp) {
        String memberHashKey = generateMemberHashKey(memberId, resumeId);

        if (shouldIncrementViewCount(memberId, clientIp, memberHashKey)) {
            incrementViewCount(resumeId, memberId, memberHashKey);
        }
    }

    public boolean shouldIncrementViewCount(Long memberId, String clientIp, String memberHashKey) {
        if (isNonMember(memberId)) {
            return isFirstTimeNonMemberView(clientIp, memberHashKey);
        }
        return isFirstTimeMemberView(memberHashKey);
    }

    public String generateMemberHashKey(Long memberId, Long resumeId) {
        return String.format("member%d%d", memberId, resumeId);
    }

    public boolean isNonMember(Long memberId) {
        return memberId == 0L;
    }

    private boolean isFirstTimeNonMemberView(String clientIp, String memberHashKey) {
        return !(Boolean.TRUE.equals(objectRedisTemplate.opsForSet().isMember(RESUME_VIEWED_NOT_MEMBER, clientIp))
                &&objectRedisTemplate.opsForHash().hasKey(RESUME_VIEWED_MEMBER_DAY, memberHashKey));
    }

    private boolean isFirstTimeMemberView(String memberHashKey) {
        return !objectRedisTemplate.opsForHash().hasKey(RESUME_VIEWED_MEMBER_DAY, memberHashKey);
    }

    private void incrementViewCount(Long resumeId, Long memberId    , String memberHashKey) {
        String redisKey = RESUME_VIEW_COUNT_PREFIX + resumeId;

        recordViewTimestamp(memberHashKey);
        incrementViewCounter(redisKey);
        log.info("First view by member {}", memberId);
    }

    private void recordViewTimestamp(String memberHashKey) {
        objectRedisTemplate.opsForHash().put(
                RESUME_VIEWED_MEMBER_DAY,
                memberHashKey,
                System.currentTimeMillis()
        );
    }

    private void incrementViewCounter(String redisKey) {
        objectRedisTemplate.opsForValue().increment(redisKey, 1L);
    }

}
