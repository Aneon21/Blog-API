package com.blog.api.security.filters;

import com.blog.api.enums.Roles;
import com.blog.api.models.Accounts;
import com.blog.api.security.principals.UserPrincipal;
import com.blog.api.security.services.JwtAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        log.info("Entered JWT AuthFilter");

        if(Objects.isNull(header) || header.trim().isEmpty() || !header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = header.substring(7);

        log.info("Jwt Token value is: {}", jwtToken);

        if(!jwtToken.isEmpty() && authService.validateJwt(jwtToken) && SecurityContextHolder.getContext().getAuthentication() == null){
            log.info("Token is validated. And authentication object is null");
            Accounts account = Accounts.builder()
                    .username("Sayak Bose")
                    .role(Roles.USER)
                    .build();

            UserPrincipal userPrincipal = new UserPrincipal(account);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
