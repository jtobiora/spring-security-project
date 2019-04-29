package com.security.demo.controller;


import com.security.demo.dto.request.LoginRequest;
import com.security.demo.dto.request.SignUpRequest;
import com.security.demo.dto.response.ApiResponse;
import com.security.demo.dto.response.JwtAuthenticationResponse;
import com.security.demo.exceptions.AppException;
import com.security.demo.service.jwt.JwtTokenProvider;
import com.security.demo.model.Role;
import com.security.demo.model.RoleName;
import com.security.demo.model.User;
import com.security.demo.service.RoleService;
import com.security.demo.service.UserService;
import com.security.demo.service.redis.TokenCacheService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenCacheService tokenCacheService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpSession httpSession) {

        //Authenticate the user request - email and password
        Authentication authenticated = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        if (authenticated.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authenticated);

            //generate JWT Token
            Map<String,String> tokenMap = tokenProvider.generateJWTToken(authenticated,httpSession);

            //save the user token in redis cache
            tokenCacheService.saveUserToken(tokenMap.get("sessionId"),tokenMap.get("token"));

            //set the user as logged
            tokenCacheService.setUserAsLogged(loginRequest.getEmail(),tokenMap.get("sessionId"));

            return ResponseEntity.ok(new JwtAuthenticationResponse(tokenMap.get("token")));
        }

        return ResponseEntity.badRequest().body("User could not be authenticated");
    }

    @GetMapping("/show/{sessionId}")
    public ResponseEntity<?> findToken(@PathVariable String sessionId){
        return ResponseEntity.ok(tokenCacheService.findUserToken(sessionId,"key"));
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if(userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleService.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userService.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
