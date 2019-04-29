package com.security.demo.controller;

import com.security.demo.service.redis.TokenCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @Autowired
    private TokenCacheService userService;


}
