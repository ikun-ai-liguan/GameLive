package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.config.AuthContext;
import com.gamelive.dto.ProfileUpdateRequest;
import com.gamelive.entity.User;
import com.gamelive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户信息 / 关注关系。
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<User> me() {
        return Result.success(userService.getCurrentUser(AuthContext.getUserId()));
    }

    @PutMapping("/me")
    public Result<Void> updateProfile(@RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(AuthContext.getUserId(), request);
        return Result.success();
    }

    @PostMapping("/follow/{anchorId}")
    public Result<Void> follow(@PathVariable Long anchorId) {
        userService.follow(AuthContext.getUserId(), anchorId);
        return Result.success();
    }

    @DeleteMapping("/follow/{anchorId}")
    public Result<Void> unfollow(@PathVariable Long anchorId) {
        userService.unfollow(AuthContext.getUserId(), anchorId);
        return Result.success();
    }

    /** 当前用户关注的主播列表 */
    @GetMapping("/follows")
    public Result<List<User>> follows() {
        return Result.success(userService.listFollows(AuthContext.getUserId()));
    }

    @GetMapping("/{anchorId}/fans")
    public Result<List<User>> fans(@PathVariable Long anchorId) {
        return Result.success(userService.listFans(anchorId));
    }
}
