package com.security.demo.service.sessions;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class SessionManager {

    private static final String KEY = "my-session:sessions:expires:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;

    @Value("${session-timeout}")
    private Long sessionTimeout;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void updateTimeout(String sessionId) {
        hashOperations.getOperations().expire(KEY+sessionId,sessionTimeout, TimeUnit.SECONDS);
    }

    public boolean isValidSession(String sessionId) {
        return hashOperations.getOperations().hasKey(KEY+sessionId);
    }

    public void deleteSession(String sessionId) {
        log.debug("Deleting user session {}",  KEY+sessionId);
        hashOperations.getOperations().delete(KEY+sessionId);
    }
}
