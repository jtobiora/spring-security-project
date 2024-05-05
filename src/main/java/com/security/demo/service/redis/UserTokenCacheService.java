package com.security.demo.service.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserTokenCacheService{

    private static final String KEY = "user-token";


    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;

    @Value("${token-timeout}")
    private int tokenTimeout;


    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public Object findUserToken(String sessionId, String userToken) {
        return hashOperations.get(KEY+sessionId, userToken);
    }

    public boolean saveUserToken(String sessionId, String userToken)
    {

        if (userToken == null){
            log.error("userToken is NULL...unable to save... returning false");
            return false;
        }
        List<String> tasks = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(tasks, type);
        hashOperations.put(KEY+sessionId, userToken , json);
        redisTemplate.expire(KEY+sessionId, tokenTimeout, TimeUnit.DAYS);
        return true;
    }


    public boolean saveUserTokenAndTask(String sessionId, String userToken, List<String> tasks) {

        if (userToken == null){
            log.error("userToken is NULL...unable to save... returning false");
            return false;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(tasks, type);

        hashOperations.put(KEY+sessionId, userToken , json);
        redisTemplate.expire(KEY+sessionId, tokenTimeout, TimeUnit.DAYS);
        return true;

    }

    public List<String> getTask(String sessionId, String userToken) {

        Object task = hashOperations.get(KEY + sessionId, userToken);

        if (task != null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> fromJson = gson.fromJson(String.valueOf(task), type);
            return fromJson;
        }

        return new ArrayList<>();
    }

    public void deleteUserToken(String userToken, String sessionId) {
        hashOperations.delete(KEY+sessionId, userToken);
    }

    public void deleteUserToken(String sessionId) {
        log.debug("deleting user token for session {}", KEY + sessionId);
        Set keys = hashOperations.keys(KEY + sessionId);
        keys.forEach(o -> {
            hashOperations.delete(KEY + sessionId, o);
        });
    }

}