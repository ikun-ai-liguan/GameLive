package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gamelive.common.BusinessException;
import com.gamelive.dto.RoomCreateRequest;
import com.gamelive.entity.LiveRoom;
import com.gamelive.mapper.LiveRoomMapper;
import com.gamelive.service.LiveRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiveRoomServiceImpl implements LiveRoomService {

    private final LiveRoomMapper liveRoomMapper;

    @Override
    public LiveRoom createRoom(Long anchorId, RoomCreateRequest request) {
        LiveRoom room = new LiveRoom();
        room.setAnchorId(anchorId);
        room.setTitle(request.getTitle());
        room.setCategoryId(request.getCategoryId());
        room.setCover(request.getCover());
        room.setNotice(request.getNotice());
        // 生成随机推流密钥，前端 / OBS 拼接 RTMP 地址使用
        room.setStreamKey(UUID.randomUUID().toString().replace("-", ""));
        room.setStatus("NOT_LIVING");
        room.setAuditStatus("PENDING");
        room.setViewerCount(0);
        room.setLikeCount(0);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        liveRoomMapper.insert(room);
        return room;
    }

    @Override
    public IPage<LiveRoom> pageRooms(Long categoryId, String keyword, long current, long size) {
        LambdaQueryWrapper<LiveRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, LiveRoom::getCategoryId, categoryId);
        wrapper.like(keyword != null && !keyword.isBlank(), LiveRoom::getTitle, keyword);
        wrapper.orderByDesc(LiveRoom::getUpdateTime);
        return liveRoomMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public LiveRoom getRoom(Long roomId) {
        LiveRoom room = liveRoomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
        return room;
    }

    @Override
    public void startLive(Long roomId) {
        LiveRoom room = getRoom(roomId);
        room.setStatus("LIVING");
        room.setUpdateTime(LocalDateTime.now());
        liveRoomMapper.updateById(room);
    }

    @Override
    public void stopLive(Long roomId) {
        LiveRoom room = getRoom(roomId);
        room.setStatus("REPLAY");
        room.setUpdateTime(LocalDateTime.now());
        liveRoomMapper.updateById(room);
        // TODO 下播后将录制文件写入 replayUrl，生成回放
    }

    @Override
    public List<LiveRoom> listReplays() {
        return liveRoomMapper.selectList(new LambdaQueryWrapper<LiveRoom>()
                .eq(LiveRoom::getStatus, "REPLAY")
                .orderByDesc(LiveRoom::getUpdateTime));
    }
}
