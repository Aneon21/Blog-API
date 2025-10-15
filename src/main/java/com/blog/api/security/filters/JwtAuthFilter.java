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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Entered JWT Authentication Filer");
        validateJwt(request);
        filterChain.doFilter(request, response);
    }

    private void validateJwt(HttpServletRequest request){
        log.info("Validating Authorization header");
        String authorizationValue = request.getHeader("Authorization");

        if(Objects.isNull(authorizationValue) || !authorizationValue.startsWith("Bearer ")){
            log.info("Authorization header is either empty, or is not of type Bearer");
            log.info("Authorization header value: {}", authorizationValue);
            return;
        }

        String token = authorizationValue.substring(7).trim();

        log.debug("JWT value: {}", token);

        if(SecurityContextHolder.getContext().getAuthentication() == null && jwtAuthService.isTokenValid(token) && !jwtAuthService.isTokenExpired(token)){
            Map<String, Object> map = jwtAuthService.getUserDetails(token);

            Accounts account = Accounts.builder()
                    .username((String) map.get("Subject"))
                    .role(getRoles((String)map.get("Role")))
                    .build();
            UserPrincipal principal = new UserPrincipal(account);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("JWT successfully validated and Security Context updated");
        }
        else{
            log.info("Improper JWT token found");
        }
    }

    private Roles getRoles(String role){
        if(role.equals("USER")){
            return Roles.USER;
        }
        else if(role.equals("ADMIN")){
            return Roles.ADMIN;
        }
        else{
            throw new RuntimeException("Role is invalid");
        }
    }
}
