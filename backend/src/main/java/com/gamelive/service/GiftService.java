package com.gamelive.service;

import com.gamelive.dto.GiftSendRequest;
import com.gamelive.entity.Gift;

import java.util.List;

public interface GiftService {

    /** 礼物字典列表 */
    List<Gift> listGifts();

    /** 发送礼物打赏 */
    void sendGift(GiftSendRequest request, Long fromUserId);
}
