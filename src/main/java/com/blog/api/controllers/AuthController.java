package com.blog.api.controllers;

import com.blog.api.mappers.requests.LoginRequest;
import com.blog.api.mappers.requests.UserRegistrationRequest;
import com.blog.api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request){
        String response = authService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest request) throws Exception{
        String jwtToken = authService.loginUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
    }
}
