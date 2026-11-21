package com.gamelive.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.common.Result;
import com.gamelive.entity.LiveRoom;
import com.gamelive.entity.User;
import com.gamelive.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 平台管理后台接口。
 * TODO 增加管理员角色校验拦截器。
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public Result<IPage<User>> users(@RequestParam(defaultValue = "1") long current,
                                     @RequestParam(defaultValue = "12") long size,
                                     @RequestParam(required = false) String keyword) {
        return Result.success(adminService.pageUsers(current, size, keyword));
    }

    @PostMapping("/users/{id}/ban")
    public Result<Void> banUser(@PathVariable Long id,
                                @RequestParam(defaultValue = "true") boolean banned,
                                @RequestParam(required = false) String reason) {
        adminService.banUser(id, banned, reason);
        return Result.success();
    }

    @GetMapping("/rooms")
    public Result<IPage<LiveRoom>> rooms(@RequestParam(defaultValue = "1") long current,
                                        @RequestParam(defaultValue = "12") long size,
                                        @RequestParam(required = false) String keyword) {
        return Result.success(adminService.pageRooms(current, size, keyword));
    }

    @PostMapping("/rooms/{id}/audit")
    public Result<Void> auditRoom(@PathVariable Long id,
                                  @RequestParam(defaultValue = "true") boolean approved,
                                  @RequestParam(required = false) String reason) {
        adminService.auditRoom(id, approved, reason);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        return Result.success(adminService.statistics());
    }
}
