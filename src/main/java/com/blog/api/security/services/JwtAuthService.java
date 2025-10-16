package com.blog.api.security.services;

import com.blog.api.security.principals.UserPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtAuthService {
    @Value("${jwt.key}")
    private String key;

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(UserPrincipal userPrincipal){
        return Jwts.builder()
                .signWith(getKey())
                .subject(userPrincipal.getUsername())
                .claim("Role", userPrincipal.getRole())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .compact();
    }

    public boolean isTokenValid(String token){
        try{
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        }
        catch (ExpiredJwtException ex){
            return true;
        }
        catch (JwtException ex){
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        try{
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);

            return false;
        }
        catch (ExpiredJwtException ex){
            return true;
        }
    }

    public Map<String, Object> getUserDetails(String token){
        Map<String, Object> map = new HashMap<>();

        var payload = Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token)
                .getPayload();

        map.put("Subject", payload.getSubject());
        map.put("Role", payload.get("Role"));

        return map;
    }

    public String generateRefreshToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24*7))
                .signWith(getKey())
                .compact();
    }

    public String getUsername(String token){
        try{
            return Jwts.parser().verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }
        catch (ExpiredJwtException e){
            return e.getClaims().getSubject();
        }
        catch (JwtException e){
            throw new RuntimeException("Invalid JWT sent");
        }
    }
}
