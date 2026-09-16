package com.platform.auth.web;

import com.platform.auth.service.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证接口（骨架）。
 *
 * - /login            账密登录（接 org-user-service 校验，TODO）
 * - /token/refresh    刷新 token
 * - /exchange/{platform} 多平台免登换发：钉钉免登码 / 企微 oauth code / 飞书 login code → 统一平台 JWT
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenService jwtTokenService;

    public AuthController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    public record TokenResponse(String accessToken, String refreshToken, long expiresIn) {
    }

    /** 多平台免登码换发统一 JWT（骨架：未接平台 SDK，返回占位实现）。 */
    @PostMapping("/exchange/{platform}")
    public ResponseEntity<TokenResponse> exchange(@org.springframework.web.bind.annotation.PathVariable String platform,
                                                  @RequestBody Map<String, String> body) {
        String code = body.getOrDefault("code", "");
        if (code.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        // TODO: 按 platform 调对应平台 API 换 userId（dingtalk/wecom/feishu），再映射平台内用户
        String userId = "pending-" + platform;
        String token = jwtTokenService.issueAccessToken(userId,
                Map.of("platform", platform, "tenantId", "default"));
        return ResponseEntity.ok(new TokenResponse(token, token, jwtTokenService.accessTtlSeconds()));
    }

    /** 刷新 token（骨架：refresh 与 access 相同，TODO 引入 refresh token 存储）。 */
    @PostMapping("/token/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        var claims = jwtTokenService.parse(refreshToken);
        String token = jwtTokenService.issueAccessToken(claims.getSubject(), claims);
        return ResponseEntity.ok(new TokenResponse(token, token, jwtTokenService.accessTtlSeconds()));
    }
}
