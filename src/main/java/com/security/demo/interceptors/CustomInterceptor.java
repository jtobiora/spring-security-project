package com.security.demo.interceptors;

import com.google.gson.Gson;
import com.security.demo.constants.SecurityConstants;
import com.security.demo.enums.Errors;
import com.security.demo.exceptions.ErrorDetails;
import com.security.demo.service.jwt.JwtTokenProvider;
import com.security.demo.security.UserPrincipal;
import com.security.demo.service.CustomUserDetailsService;
import com.security.demo.service.redis.TokenCacheService;
import com.security.demo.service.sessions.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
@Slf4j
public class CustomInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TokenCacheService tokenCacheService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private SessionManager sessionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            String jwtToken = getJwtFromRequest(request);
            if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken)) {
                Long userId = tokenProvider.getUserIdFromJWT(jwtToken);

                UserPrincipal principal = tokenProvider.decodeToken(jwtToken);
                if (principal == null){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), Errors.UNKNOWN_USER.getValue())));
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
                    return false;
                }

                if (!sessionManager.isValidSession(principal.getSessionId())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_FORBIDDEN),Errors.EXPIRED_SESSION.getValue())));
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
                    return false;
                }

                if (!tokenCacheService.isValidUserToken(jwtToken, principal.getSessionId())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(), String.valueOf(HttpServletResponse.SC_FORBIDDEN), Errors.EXPIRED_TOKEN.getValue())));
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                    return false;
                }

                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                sessionManager.updateTimeout(principal.getSessionId());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return true;
            }else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(new Gson().toJson(new ErrorDetails(new Date(),String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), Errors.UNKNOWN_USER.getValue())));
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
                return false;
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            return false;
        }
    }

    //handles requests after they are post processed
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    //add authenticated user to the spring context
//    private void addToSecurityContextAndServletRequest(UserDetail userDetail, HttpServletRequest request){
//        request.setAttribute(Constants.USER_DETAIL, userDetail);
//
//        String username = userDetail.getEmailAddress();
//        log.trace("user detail from token: {}", userDetail);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.NO_AUTHORITIES);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
