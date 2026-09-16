package com.platform.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * JWT 签发与校验（骨架实现）。
 * 所有终端（PC/H5/钉钉/企微/飞书容器）统一换取同一种平台 JWT。
 */
@Service
public class JwtTokenService {

    private final SecretKey key;
    private final long accessTtlMinutes;
    private final long refreshTtlDays;

    public JwtTokenService(com.platform.auth.config.JwtProperties props) {
        String secret = props.secret();
        if (secret == null || secret.length() < 32) {
            // 骨架阶段兜底；生产必须配置强密钥
            secret = "dev-only-secret-key-change-me-in-production!!";
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMinutes = props.accessTtlMinutes();
        this.refreshTtlDays = props.refreshTtlDays();
    }

    /** 签发 access token。claims 至少含 userId、tenantId、platform。 */
    public String issueAccessToken(String userId, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlMinutes * 60)))
                .signWith(key)
                .compact();
    }

    /** 校验并解析 token，非法/过期抛 JwtException。 */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    public long accessTtlSeconds() {
        return accessTtlMinutes * 60;
    }

    public long refreshTtlSeconds() {
        return refreshTtlDays * 24 * 3600;
    }
}
