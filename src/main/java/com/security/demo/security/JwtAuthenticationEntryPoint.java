package com.security.demo.security;

import com.google.gson.Gson;
import com.security.demo.enums.Errors;
import com.security.demo.exceptions.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req,
                         HttpServletResponse res,
                         AuthenticationException e) throws IOException, ServletException {
        log.error("User is unauthorized. Message - {}", e.getMessage());
        //httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), Errors.UNAUTHORIZED.getValue())));
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));
    }
}
