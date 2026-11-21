package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.dto.ProfileUpdateRequest;
import com.gamelive.entity.User;
import com.gamelive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户信息 / 关注关系。
 * TODO 接入登录拦截器后，从 Token 解析当前用户 ID，替换 X-User-Id 头。
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<User> me(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return Result.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/me")
    public Result<Void> updateProfile(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                      @RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(userId, request);
        return Result.success();
    }

    @PostMapping("/follow/{anchorId}")
    public Result<Void> follow(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                               @PathVariable Long anchorId) {
        userService.follow(userId, anchorId);
        return Result.success();
    }

    @DeleteMapping("/follow/{anchorId}")
    public Result<Void> unfollow(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                 @PathVariable Long anchorId) {
        userService.unfollow(userId, anchorId);
        return Result.success();
    }

    @GetMapping("/{anchorId}/fans")
    public Result<List<User>> fans(@PathVariable Long anchorId) {
        return Result.success(userService.listFans(anchorId));
    }
}
