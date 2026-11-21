package com.gamelive.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.common.Result;
import com.gamelive.dto.RoomCreateRequest;
import com.gamelive.entity.LiveRoom;
import com.gamelive.service.LiveRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 直播间。
 */
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class LiveRoomController {

    private final LiveRoomService liveRoomService;

    /** 直播列表：分类筛选 + 关键词搜索 + 分页 */
    @GetMapping
    public Result<IPage<LiveRoom>> list(@RequestParam(required = false) Long categoryId,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(defaultValue = "1") long current,
                                        @RequestParam(defaultValue = "12") long size) {
        return Result.success(liveRoomService.pageRooms(categoryId, keyword, current, size));
    }

    @GetMapping("/{id}")
    public Result<LiveRoom> detail(@PathVariable Long id) {
        return Result.success(liveRoomService.getRoom(id));
    }

    /** 主播创建直播间（自动生成推流密钥） */
    @PostMapping
    public Result<LiveRoom> create(@RequestHeader(value = "X-User-Id", required = false) Long anchorId,
                                  @Valid @RequestBody RoomCreateRequest request) {
        return Result.success(liveRoomService.createRoom(anchorId, request));
    }

    @PostMapping("/{id}/start")
    public Result<Void> start(@PathVariable Long id) {
        liveRoomService.startLive(id);
        return Result.success();
    }

    @PostMapping("/{id}/stop")
    public Result<Void> stop(@PathVariable Long id) {
        liveRoomService.stopLive(id);
        return Result.success();
    }

    @GetMapping("/replays")
    public Result<List<LiveRoom>> replays() {
        return Result.success(liveRoomService.listReplays());
    }
}
