package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gamelive.common.BusinessException;
import com.gamelive.entity.BanRecord;
import com.gamelive.entity.LiveRoom;
import com.gamelive.entity.User;
import com.gamelive.mapper.BanRecordMapper;
import com.gamelive.mapper.LiveRoomMapper;
import com.gamelive.mapper.UserMapper;
import com.gamelive.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final LiveRoomMapper liveRoomMapper;
    private final BanRecordMapper banRecordMapper;

    @Override
    public IPage<User> pageUsers(long current, long size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.isBlank(), User::getUsername, keyword)
                .or().like(keyword != null && !keyword.isBlank(), User::getNickname, keyword);
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public void banUser(Long userId, boolean banned, String reason) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(banned ? "BANNED" : "NORMAL");
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        BanRecord record = new BanRecord();
        record.setTargetUserId(userId);
        record.setType(banned ? "BAN" : "MUTE");
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        banRecordMapper.insert(record);
    }

    @Override
    public IPage<LiveRoom> pageRooms(long current, long size, String keyword) {
        LambdaQueryWrapper<LiveRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.isBlank(), LiveRoom::getTitle, keyword);
        wrapper.orderByDesc(LiveRoom::getUpdateTime);
        return liveRoomMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public void auditRoom(Long roomId, boolean approved, String reason) {
        LiveRoom room = liveRoomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
        room.setAuditStatus(approved ? "APPROVED" : "REJECTED");
        room.setUpdateTime(LocalDateTime.now());
        liveRoomMapper.updateById(room);
        // TODO 审核驳回时通知主播，reason 记录驳回原因
    }

    @Override
    public Map<String, Object> statistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("roomCount", liveRoomMapper.selectCount(null));
        stats.put("livingCount", liveRoomMapper.selectCount(
                new LambdaQueryWrapper<LiveRoom>().eq(LiveRoom::getStatus, "LIVING")));
        stats.put("bannedUserCount", userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, "BANNED")));
        return stats;
    }
}
