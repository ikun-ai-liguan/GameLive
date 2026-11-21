package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.entity.Danmu;
import com.gamelive.service.DanmuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 弹幕历史记录（实时推送走 WebSocket）。
 */
@RestController
@RequestMapping("/rooms/{roomId}/danmu")
@RequiredArgsConstructor
public class DanmuController {

    private final DanmuService danmuService;

    @GetMapping
    public Result<List<Danmu>> history(@PathVariable Long roomId) {
        return Result.success(danmuService.listByRoom(roomId));
    }
}
