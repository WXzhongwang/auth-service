package com.platform.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 签发配置。
 * secret 生产环境必须从配置中心/环境变量注入，禁止写死。
 */
@ConfigurationProperties(prefix = "platform.auth.jwt")
public record JwtProperties(
        String secret,
        long accessTtlMinutes,
        long refreshTtlDays
) {
    public JwtProperties {
        if (accessTtlMinutes <= 0) {
            accessTtlMinutes = 120;
        }
        if (refreshTtlDays <= 0) {
            refreshTtlDays = 7;
        }
    }
}
