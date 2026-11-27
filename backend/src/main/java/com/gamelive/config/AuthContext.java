package com.gamelive.config;

import com.gamelive.common.BusinessException;
import com.gamelive.common.ResultCode;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 从请求属性中读取当前登录用户 ID。
 */
public final class AuthContext {

    private AuthContext() {
    }

    /** 获取当前用户 ID；未登录时抛出 401 业务异常 */
    public static Long getUserId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            Object userId = servletAttributes.getRequest().getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
            if (userId instanceof Long id) {
                return id;
            }
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED);
    }
}
