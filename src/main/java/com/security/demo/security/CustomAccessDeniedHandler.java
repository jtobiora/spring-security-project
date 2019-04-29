package com.security.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.security.demo.enums.Errors;
import com.security.demo.exceptions.ErrorDetails;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req,
                       HttpServletResponse res,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_FORBIDDEN), Errors.NOT_PERMITTED.getValue())));
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));
    }
}
