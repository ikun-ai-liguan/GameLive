package com.gamelive.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.common.Result;
import com.gamelive.config.AuthContext;
import com.gamelive.entity.Notification;
import com.gamelive.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站内通知。
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Result<IPage<Notification>> list(@RequestParam(defaultValue = "1") long current,
                                            @RequestParam(defaultValue = "12") long size) {
        return Result.success(notificationService.pageNotifications(AuthContext.getUserId(), current, size));
    }

    @PostMapping("/{id}/read")
    public Result<Void> read(@PathVariable Long id) {
        notificationService.markRead(id, AuthContext.getUserId());
        return Result.success();
    }
}
