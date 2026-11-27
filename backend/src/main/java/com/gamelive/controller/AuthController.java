package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.dto.LoginRequest;
import com.gamelive.dto.RegisterRequest;
import com.gamelive.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册 / 登录。
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    /** 登录成功直接返回 JWT 字符串，前端以 Authorization: Bearer &lt;token&gt; 携带 */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }
}
