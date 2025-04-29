package com.wanderly.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public boolean isEmailBlocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("register:" + email));
    }

    public void blockEmail(String email) {
        redisTemplate.opsForValue().set("register:" + email, "BLOCKED", 60, TimeUnit.SECONDS);
    }

    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set("verification:" + email, code, 15, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get("verification:" + email);
    }

    public void deleteVerificationCode(String email) {
        redisTemplate.delete("verification:" + email);
    }
}
