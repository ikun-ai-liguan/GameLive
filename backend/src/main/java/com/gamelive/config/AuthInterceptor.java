package com.gamelive.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelive.common.Result;
import com.gamelive.common.ResultCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 从 Authorization: Bearer &lt;token&gt; 解析用户 ID 并写入 request 属性；
 * 未携带令牌时兼容旧的 X-User-Id 请求头。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID_ATTRIBUTE = "userId";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // CORS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            String token = authorization.substring(BEARER_PREFIX.length()).trim();
            try {
                request.setAttribute(USER_ID_ATTRIBUTE, jwtUtil.getUserId(token));
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("JWT 解析失败：{}", e.getMessage());
                writeUnauthorized(response, "登录已过期或令牌无效");
                return false;
            }
        }

        // 向后兼容：仍支持骨架阶段的 X-User-Id 请求头
        String legacyUserId = request.getHeader("X-User-Id");
        if (legacyUserId != null && !legacyUserId.isBlank()) {
            try {
                request.setAttribute(USER_ID_ATTRIBUTE, Long.valueOf(legacyUserId.trim()));
            } catch (NumberFormatException e) {
                writeUnauthorized(response, "X-User-Id 格式错误");
                return false;
            }
        }
        // 未携带身份信息时放行，由具体接口通过 AuthContext 决定是否要求登录
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Result.fail(ResultCode.UNAUTHORIZED.getCode(), message));
    }
}
