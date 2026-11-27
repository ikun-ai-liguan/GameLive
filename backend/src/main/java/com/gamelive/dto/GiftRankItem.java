package com.gamelive.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 直播间礼物榜条目。
 */
@Data
public class GiftRankItem {

    private Long userId;

    private String nickname;

    /** 赠送礼物总数量 */
    private Integer totalCount;

    /** 打赏总金额 = sum(gift_count * price) */
    private BigDecimal totalAmount;
}
