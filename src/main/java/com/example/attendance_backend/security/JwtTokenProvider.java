package com.example.attendance_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long expirationTime;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationTime = expirationTime;
    }

    // ✅ 토큰 생성
    public String generateToken(Long memberId, String role, String name) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))     // 사용자 ID
                .claim("role", role)                      // 역할: STUDENT / ADMIN
                .claim("name", name)                      // 이름 (optional)
                .setIssuedAt(new Date())                  // 발급 시각
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시각
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    // ✅ 사용자 ID 조회
    public Long getMemberId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    // ✅ 역할(role) 조회
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ✅ 이름(name) 조회 (선택)
    public String getName(String token) {
        return getClaims(token).get("name", String.class);
    }

    // ✅ 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ 내부용: Claim 파싱
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}