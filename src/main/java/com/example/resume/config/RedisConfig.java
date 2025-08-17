package com.example.resume.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    public static final String RESUME_VIEW_COUNT_PREFIX = "resumeViewCount:";
    public static final String RESUME_VIEWED_MEMBER_DAY = "viewedMemberDay:";
    public static final String RESUME_VIEWED_NOT_MEMBER = "viewedIp:";
    public static final String RESUME_RECENTLY_VIEWED_KEY = "recent:user:";

    @Bean
    LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 기반 직렬화
        return template;
    }
}
