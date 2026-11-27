package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gamelive.common.BusinessException;
import com.gamelive.dto.HistoryItem;
import com.gamelive.entity.LiveRoom;
import com.gamelive.entity.User;
import com.gamelive.entity.WatchHistory;
import com.gamelive.mapper.LiveRoomMapper;
import com.gamelive.mapper.UserMapper;
import com.gamelive.mapper.WatchHistoryMapper;
import com.gamelive.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchHistoryServiceImpl implements WatchHistoryService {

    private final WatchHistoryMapper watchHistoryMapper;
    private final LiveRoomMapper liveRoomMapper;
    private final UserMapper userMapper;

    @Override
    public void record(Long roomId, Long userId) {
        if (liveRoomMapper.selectById(roomId) == null) {
            throw new BusinessException("直播间不存在");
        }
        WatchHistory exist = watchHistoryMapper.selectOne(new LambdaQueryWrapper<WatchHistory>()
                .eq(WatchHistory::getUserId, userId)
                .eq(WatchHistory::getRoomId, roomId));
        LocalDateTime now = LocalDateTime.now();
        if (exist == null) {
            WatchHistory history = new WatchHistory();
            history.setUserId(userId);
            history.setRoomId(roomId);
            history.setCreateTime(now);
            watchHistoryMapper.insert(history);
        } else {
            exist.setCreateTime(now);
            watchHistoryMapper.updateById(exist);
        }
    }

    @Override
    public IPage<HistoryItem> pageHistory(Long userId, long current, long size) {
        IPage<WatchHistory> page = watchHistoryMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<WatchHistory>()
                        .eq(WatchHistory::getUserId, userId)
                        .orderByDesc(WatchHistory::getCreateTime));

        List<Long> roomIds = page.getRecords().stream()
                .map(WatchHistory::getRoomId).distinct().collect(Collectors.toList());
        Map<Long, LiveRoom> roomMap = new HashMap<>();
        if (!roomIds.isEmpty()) {
            roomMap = liveRoomMapper.selectBatchIds(roomIds).stream()
                    .collect(Collectors.toMap(LiveRoom::getId, Function.identity(), (a, b) -> a));
        }
        final Map<Long, LiveRoom> rooms = roomMap;
        List<Long> anchorIds = rooms.values().stream()
                .map(LiveRoom::getAnchorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, User> anchorMap = new HashMap<>();
        if (!anchorIds.isEmpty()) {
            anchorMap = userMapper.selectBatchIds(anchorIds).stream()
                    .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        }
        final Map<Long, User> anchors = anchorMap;

        List<HistoryItem> items = page.getRecords().stream().map(history -> {
            HistoryItem item = new HistoryItem();
            item.setRoomId(history.getRoomId());
            item.setWatchedAt(history.getCreateTime());
            LiveRoom room = rooms.get(history.getRoomId());
            if (room != null) {
                item.setTitle(room.getTitle());
                item.setCover(room.getCover());
                item.setAnchorId(room.getAnchorId());
                User anchor = anchors.get(room.getAnchorId());
                if (anchor != null) {
                    item.setAnchorNickname(anchor.getNickname() == null ? anchor.getUsername() : anchor.getNickname());
                }
            }
            return item;
        }).collect(Collectors.toList());

        Page<HistoryItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(items);
        return result;
    }
}
