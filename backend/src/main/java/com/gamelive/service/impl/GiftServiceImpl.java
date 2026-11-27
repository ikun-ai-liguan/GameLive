package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gamelive.common.BusinessException;
import com.gamelive.dto.GiftRankItem;
import com.gamelive.dto.GiftSendRequest;
import com.gamelive.entity.Gift;
import com.gamelive.entity.GiftRecord;
import com.gamelive.entity.LiveRoom;
import com.gamelive.entity.User;
import com.gamelive.mapper.GiftMapper;
import com.gamelive.mapper.GiftRecordMapper;
import com.gamelive.mapper.LiveRoomMapper;
import com.gamelive.mapper.UserMapper;
import com.gamelive.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftServiceImpl implements GiftService {

    private final GiftMapper giftMapper;
    private final GiftRecordMapper giftRecordMapper;
    private final LiveRoomMapper liveRoomMapper;
    private final UserMapper userMapper;

    @Override
    public List<Gift> listGifts() {
        return giftMapper.selectList(null);
    }

    @Override
    public void sendGift(GiftSendRequest request, Long fromUserId) {
        Gift gift = giftMapper.selectById(request.getGiftId());
        if (gift == null) {
            throw new BusinessException("礼物不存在");
        }
        LiveRoom room = liveRoomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
        GiftRecord record = new GiftRecord();
        record.setRoomId(room.getId());
        record.setFromUserId(fromUserId);
        record.setAnchorId(room.getAnchorId());
        record.setGiftId(gift.getId());
        record.setGiftCount(request.getGiftCount());
        record.setCreateTime(LocalDateTime.now());
        giftRecordMapper.insert(record);
    }

    @Override
    public List<GiftRankItem> giftRank(Long roomId) {
        if (liveRoomMapper.selectById(roomId) == null) {
            throw new BusinessException("直播间不存在");
        }
        List<GiftRecord> records = giftRecordMapper.selectList(new LambdaQueryWrapper<GiftRecord>()
                .eq(GiftRecord::getRoomId, roomId));
        if (records.isEmpty()) {
            return List.of();
        }

        List<Long> giftIds = records.stream().map(GiftRecord::getGiftId)
                .distinct().collect(Collectors.toList());
        Map<Long, BigDecimal> priceById = new HashMap<>();
        for (Gift gift : giftMapper.selectBatchIds(giftIds)) {
            priceById.put(gift.getId(), gift.getPrice() == null ? BigDecimal.ZERO : gift.getPrice());
        }

        Map<Long, Integer> countByUser = new HashMap<>();
        Map<Long, BigDecimal> amountByUser = new HashMap<>();
        for (GiftRecord record : records) {
            int count = record.getGiftCount() == null ? 0 : record.getGiftCount();
            BigDecimal price = priceById.getOrDefault(record.getGiftId(), BigDecimal.ZERO);
            countByUser.merge(record.getFromUserId(), count, Integer::sum);
            amountByUser.merge(record.getFromUserId(), price.multiply(BigDecimal.valueOf(count)), BigDecimal::add);
        }

        List<Long> userIds = new ArrayList<>(countByUser.keySet());
        Map<Long, User> users = new HashMap<>();
        if (!userIds.isEmpty()) {
            for (User user : userMapper.selectBatchIds(userIds)) {
                users.put(user.getId(), user);
            }
        }

        List<GiftRankItem> items = countByUser.keySet().stream().map(userId -> {
            GiftRankItem item = new GiftRankItem();
            item.setUserId(userId);
            item.setTotalCount(countByUser.get(userId));
            item.setTotalAmount(amountByUser.get(userId));
            User user = users.get(userId);
            if (user != null) {
                item.setNickname(user.getNickname() == null ? user.getUsername() : user.getNickname());
            }
            return item;
        }).collect(Collectors.toList());

        items.sort((a, b) -> {
            int byAmount = b.getTotalAmount().compareTo(a.getTotalAmount());
            return byAmount != 0 ? byAmount : Integer.compare(b.getTotalCount(), a.getTotalCount());
        });
        return items;
    }
}
