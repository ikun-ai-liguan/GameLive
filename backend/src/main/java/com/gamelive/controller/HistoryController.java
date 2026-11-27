package com.gamelive.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.common.Result;
import com.gamelive.config.AuthContext;
import com.gamelive.dto.HistoryItem;
import com.gamelive.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 观看历史。
 */
@RestController
@RequiredArgsConstructor
public class HistoryController {

    private final WatchHistoryService watchHistoryService;

    @PostMapping("/rooms/{roomId}/history")
    public Result<Void> record(@PathVariable Long roomId) {
        watchHistoryService.record(roomId, AuthContext.getUserId());
        return Result.success();
    }

    @GetMapping("/users/history")
    public Result<IPage<HistoryItem>> history(@RequestParam(defaultValue = "1") long current,
                                              @RequestParam(defaultValue = "12") long size) {
        return Result.success(watchHistoryService.pageHistory(AuthContext.getUserId(), current, size));
    }
}
