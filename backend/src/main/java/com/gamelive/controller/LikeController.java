package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.config.AuthContext;
import com.gamelive.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 直播间点赞。
 */
@RestController
@RequestMapping("/rooms/{roomId}")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public Result<Void> like(@PathVariable Long roomId) {
        likeService.like(roomId, AuthContext.getUserId());
        return Result.success();
    }

    @GetMapping("/liked")
    public Result<Boolean> liked(@PathVariable Long roomId) {
        return Result.success(likeService.liked(roomId, AuthContext.getUserId()));
    }
}
