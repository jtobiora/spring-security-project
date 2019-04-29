package com.security.demo.service;

import com.security.demo.model.User;
import com.security.demo.repo.UserRepository;
import com.security.demo.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
//    private RedisTemplate<String,Object> redisTemplate;
//    private HashOperations hashOperations;
//
//    public UserService(RedisTemplate<String,Object> redisTemplate){
//       this.redisTemplate = redisTemplate;
//       hashOperations = redisTemplate.opsForHash();
//    }

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String username, String email){
        return userRepository.findByUsernameOrEmail(username,email);
    }

    public List<User> findByIdIn(List<Long> userIds){
        return userRepository.findByIdIn(userIds);
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public Boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

}
