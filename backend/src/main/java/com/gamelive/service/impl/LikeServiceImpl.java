package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gamelive.common.BusinessException;
import com.gamelive.entity.LiveRoom;
import com.gamelive.entity.RoomLike;
import com.gamelive.mapper.LiveRoomMapper;
import com.gamelive.mapper.RoomLikeMapper;
import com.gamelive.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final RoomLikeMapper roomLikeMapper;
    private final LiveRoomMapper liveRoomMapper;

    @Override
    public void like(Long roomId, Long userId) {
        LiveRoom room = liveRoomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
        Long count = roomLikeMapper.selectCount(new LambdaQueryWrapper<RoomLike>()
                .eq(RoomLike::getRoomId, roomId)
                .eq(RoomLike::getUserId, userId));
        if (count != null && count > 0) {
            return;
        }
        RoomLike like = new RoomLike();
        like.setRoomId(roomId);
        like.setUserId(userId);
        like.setCreateTime(LocalDateTime.now());
        roomLikeMapper.insert(like);
        room.setLikeCount((room.getLikeCount() == null ? 0 : room.getLikeCount()) + 1);
        liveRoomMapper.updateById(room);
    }

    @Override
    public boolean liked(Long roomId, Long userId) {
        Long count = roomLikeMapper.selectCount(new LambdaQueryWrapper<RoomLike>()
                .eq(RoomLike::getRoomId, roomId)
                .eq(RoomLike::getUserId, userId));
        return count != null && count > 0;
    }
}
