package com.security.demo.service.redis;

import com.security.demo.service.sessions.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserLoginCacheService {

    private static final String KEY = "userCmmsLogin";


    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;


    private final SessionManager sessionManager;

    @Value("${token-timeout}")
    private int tokenTimeout;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public String findSession(String username) {
        return (String)hashOperations.get(KEY+username, username);
    }

    public boolean isUserLogged(String username){

        //get the session id by username
        String session = findSession(username);
        if (session == null){
            //user is not logged in
            return false;
        }

        if (sessionManager.isValidSession(session)) {
            //user is still logged in
            return true;
        }

        return false;
    }

    public String getLoggedUserSession(String username){
        //get the session id by username
        String session = findSession(username);
        if (session == null){
            //user is not logged in
            return null;
        }

        if (sessionManager.isValidSession(session)) {
            //user is still logged in
            return session;
        }

        return null;
    }

    public boolean setUserAsLogged(String username, String sessionId) {
        hashOperations.put(KEY+username, username, sessionId);
        redisTemplate.expire(KEY+username, tokenTimeout, TimeUnit.DAYS);
        return true;
    }

    public void setUserAsNotLogged(String username) {

        hashOperations.delete(KEY+username, username);
    }

}

