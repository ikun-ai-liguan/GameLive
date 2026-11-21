package com.gamelive.service.impl;

import com.gamelive.common.BusinessException;
import com.gamelive.dto.GiftSendRequest;
import com.gamelive.entity.Gift;
import com.gamelive.entity.GiftRecord;
import com.gamelive.entity.LiveRoom;
import com.gamelive.mapper.GiftMapper;
import com.gamelive.mapper.GiftRecordMapper;
import com.gamelive.mapper.LiveRoomMapper;
import com.gamelive.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftServiceImpl implements GiftService {

    private final GiftMapper giftMapper;
    private final GiftRecordMapper giftRecordMapper;
    private final LiveRoomMapper liveRoomMapper;

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
        // TODO 接入真实支付 / 虚拟币扣减与主播分成结算
    }
}
