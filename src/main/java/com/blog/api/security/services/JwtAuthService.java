package com.blog.api.security.services;

import com.blog.api.security.principals.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtAuthService {
    @Value("${jwt.key}")
    private String key;

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String getToken(UserPrincipal userPrincipal){
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("Role", userPrincipal.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .signWith(getKey())
                .compact();
    }

    public boolean validateJwt(String token){
        try{
            Jwts.parser().verifyWith(getKey()).build()
                    .parseSignedClaims(token);
        }
        catch (Exception ex){
            return false;
        }

        return true;
    }
}
