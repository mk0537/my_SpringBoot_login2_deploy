package com.example.login2.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

    private Key key;
    
    // private final long tokenValidity = 1000 * 10; // 테스트용 10초
    private final long tokenValidity = 1000 * 60 * 60 * 24; // 토큰 유효기간 24시간

    private static final String SECRET_KEY = "my-super-secret-key-that-is-at-least-32-bytes-long";

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    
    // 토큰 생성
    public String createToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(key)
                .compact();
    }

    
    // 토큰 심사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
            
            return true;
        } catch (Exception e) {
            System.out.println("토큰 검증 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }

    
    // 이메일에 따라 토큰 발급
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
        		.setSigningKey(key)
        		.build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
