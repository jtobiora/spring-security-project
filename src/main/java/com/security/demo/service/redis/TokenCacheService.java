package com.security.demo.service.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.security.demo.service.sessions.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class TokenCacheService {
    private static final String KEY = "user-token";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SessionManager sessionManager;

    private HashOperations hashOperations;

    @Value("${token-timeout}")
    private int tokenTimeout;


    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public Object findUserToken(String sessionId, String userToken) {
        return hashOperations.get(KEY+sessionId, sessionId);
    }

    public boolean saveUserToken(String sessionId, String userToken) {

        if (StringUtils.isEmpty(userToken)){
            log.error("token is null or undefined");
            return false;
        }
        hashOperations.put(KEY+sessionId, sessionId , userToken);
        redisTemplate.expire(KEY+sessionId, tokenTimeout, TimeUnit.SECONDS);
        return true;
    }

    public boolean setUserAsLogged(String username, String sessionId) {
        hashOperations.put(KEY+username, username, sessionId);
        redisTemplate.expire(KEY+username, tokenTimeout, TimeUnit.DAYS);
        return true;
    }

    public boolean isValidUserToken(String userToken, String sessionId) {
        Object token = this.findUserToken(sessionId, userToken);
        return token != null;
    }

    public void deleteUserToken(String userToken, String sessionId) {
        hashOperations.delete(KEY+sessionId, userToken);
    }

    public void deleteUserToken(String sessionId) {
        Set keys = hashOperations.keys(KEY + sessionId);
        keys.forEach(o -> {
            hashOperations.delete(KEY + sessionId, o);
        });
    }


}
