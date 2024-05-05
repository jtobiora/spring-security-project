package com.security.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;


/**
 * With this configuration, sessions will expire after 10 minutes of inactivity, and session attributes will be stored
 * in Redis under the namespace "my_session".
 * ***/
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 600, redisNamespace = "my-session")
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

}