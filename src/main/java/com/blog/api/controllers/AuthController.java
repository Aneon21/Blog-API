package com.blog.api.controllers;

import com.blog.api.mappers.requests.LoginRequest;
import com.blog.api.mappers.requests.UserRegistrationRequest;
import com.blog.api.mappers.responses.TokenResponse;
import com.blog.api.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RedisTemplate<Object, Object> redisTemplate;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request){
        log.info("Register request received for username: {}", request.getUsername());
        String response = authService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest request) throws Exception{
        log.info("Login request received for username: {}", request.getUsername());
        TokenResponse jwtToken = authService.loginUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.refreshToken(request));
    }
}
