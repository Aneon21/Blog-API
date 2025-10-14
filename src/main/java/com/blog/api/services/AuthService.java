package com.blog.api.services;

import com.blog.api.enums.Roles;
import com.blog.api.mappers.requests.LoginRequest;
import com.blog.api.mappers.requests.UserRegistrationRequest;
import com.blog.api.models.Accounts;
import com.blog.api.models.Users;
import com.blog.api.repositories.AccountsRepository;
import com.blog.api.repositories.UsersRepository;
import com.blog.api.security.principals.UserPrincipal;
import com.blog.api.security.services.JwtAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountsRepository accountsRepository;
    private final UsersRepository usersRepository;
    private final JwtAuthService jwtAuthService;
    private final AuthenticationManager authenticationManager;

    public String registerUser(UserRegistrationRequest request){
        Accounts account = Accounts.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(Roles.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .refreshToken("johncena")
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

    public String loginUser(LoginRequest request) throws Exception{

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        if(auth.isAuthenticated()){
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            return jwtAuthService.getToken(userPrincipal);
        }
        else{
            throw new Exception("Failure");
        }
    }
}
