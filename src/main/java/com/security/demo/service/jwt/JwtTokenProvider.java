package com.security.demo.service.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.demo.security.UserPrincipal;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.*;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public Map<String,String> generateJWTToken(Authentication authentication, HttpSession httpSession){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        userPrincipal.setSessionId(httpSession.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(userPrincipal);

        Map<String, String> tokenMap = new HashMap<>();

        String token = Jwts.builder()
                .setPayload(jsonNode.toString())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        tokenMap.put("token",token);
        tokenMap.put("sessionId",userPrincipal.getSessionId());

        return tokenMap;
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(String.valueOf(claims.get("id")));
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody();
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public UserPrincipal decodeToken(String token) {
        if (token != null) {
            Claims userClaims = null;

            try {
                userClaims = (Claims)Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            } catch (ExpiredJwtException var4) {
                log.error("Unable to parse token", var4);
                return null;
            } catch (UnsupportedJwtException var5) {
                log.error("Unable to parse token", var5);
                return null;
            } catch (MalformedJwtException var6) {
                log.error("Unable to parse token", var6);
                return null;
            } catch (SignatureException var7) {
                log.error("Unable to parse token", var7);
                return null;
            } catch (IllegalArgumentException var8) {
                log.error("Unable to parse token", var8);
                return null;
            } catch (Exception var9) {
                log.error("Unable to parse token", var9);
                return null;
            }

            UserPrincipal userPrincipal = new UserPrincipal();
            userPrincipal.setId(Long.valueOf(String.valueOf(userClaims.get("id"))));
            userPrincipal.setEmail((String)userClaims.get("email", String.class));
            userPrincipal.setName((String)userClaims.get("name", String.class));
            userPrincipal.setUsername((String)userClaims.get("username", String.class));
            userPrincipal.setSessionId((String)userClaims.get("sessionId", String.class));
            userPrincipal.setAuthorities((Collection)userClaims.get("authorities", ArrayList.class));

            return userPrincipal.getId() != null ? userPrincipal : null;
        } else {
            return null;
        }
    }
}
