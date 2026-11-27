package com.gamelive.service;

import com.gamelive.dto.GiftRankItem;
import com.gamelive.dto.GiftSendRequest;
import com.gamelive.entity.Gift;

import java.util.List;

public interface GiftService {

    /** 礼物字典列表 */
    List<Gift> listGifts();

    /** 发送礼物打赏 */
    void sendGift(GiftSendRequest request, Long fromUserId);

    /** 直播间礼物榜：按打赏金额降序 */
    List<GiftRankItem> giftRank(Long roomId);
}
