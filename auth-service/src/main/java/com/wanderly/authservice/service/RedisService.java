package com.wanderly.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final String registerKey = "register:";
    private static final String verificationKey = "verification:";

    public boolean isEmailBlocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(registerKey + email));
    }

    public void blockEmail(String email) {
        redisTemplate.opsForValue().set(registerKey + email, "BLOCKED", 60, TimeUnit.SECONDS);
    }

    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(verificationKey + email, code, 15, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(verificationKey + email);
    }

    public void deleteVerificationCode(String email) {
        redisTemplate.delete(verificationKey + email);
    }
}
