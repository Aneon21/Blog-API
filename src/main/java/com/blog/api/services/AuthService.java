package com.blog.api.services;

import com.blog.api.enums.Roles;
import com.blog.api.mappers.requests.LoginRequest;
import com.blog.api.mappers.requests.UserRegistrationRequest;
import com.blog.api.mappers.responses.TokenResponse;
import com.blog.api.models.Accounts;
import com.blog.api.models.Users;
import com.blog.api.repositories.AccountsRepository;
import com.blog.api.repositories.UsersRepository;
import com.blog.api.security.principals.UserPrincipal;
import com.blog.api.security.services.JwtAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AccountsRepository accountsRepository;
    private final UsersRepository usersRepository;
    private final JwtAuthService jwtAuthService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    public String registerUser(UserRegistrationRequest request){
        Accounts account = Accounts.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Roles.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .refreshToken(jwtAuthService.generateRefreshToken(request.getUsername()))
                .build();

        Users user = Users.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .bio(request.getBio())
                .account(account)
                .build();

        usersRepository.save(user);

        return "User Created";
    }

    public TokenResponse loginUser(LoginRequest request) throws Exception{

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        if(auth.isAuthenticated()){
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            return new TokenResponse(jwtAuthService.generateJwtToken(userPrincipal), "10 minutes");
        }
        else{
            throw new Exception("Failure");
        }
    }

    public TokenResponse refreshToken(HttpServletRequest request){
        Cookie cookie = Arrays.stream(Optional.ofNullable(request.getCookies())
                .orElseThrow(() -> new RuntimeException("No cookies sent")))
                .filter(c -> c.getName().equals("refresh-token"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No refresh token sent"));

        String bearer = request.getHeader("Authorization");

        if(Objects.isNull(bearer) || !bearer.startsWith("Bearer ")){
            throw new RuntimeException("No bearer authorization token found");
        }

        String token = bearer.substring(7).trim();

        Accounts account = accountsRepository.findByUsername(jwtAuthService.getUsername(token)).
                orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

        if(cookie.getValue().equals(account.getRefreshToken())
                && jwtAuthService.isTokenValid(account.getRefreshToken())
                && !jwtAuthService.isTokenExpired(account.getRefreshToken())){
            String refreshToken = addRefreshTokenToDB(account.getUsername());
            return new TokenResponse(jwtAuthService.generateJwtToken(new UserPrincipal(account)), "10 minutes");
        }
        else{
            throw new RuntimeException("Invalid refresh token sent");
        }
    }

    @Transactional
    private String addRefreshTokenToDB(String username){
        Accounts account = accountsRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found")
        );

        String refreshToken = jwtAuthService.generateRefreshToken(username);
        account.setRefreshToken(refreshToken);
        accountsRepository.save(account);

        return refreshToken;
    }
}
