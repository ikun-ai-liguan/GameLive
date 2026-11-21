package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 礼物打赏记录。
 */
@Data
@TableName("gift_record")
public class GiftRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 直播间 ID */
    private Long roomId;

    /** 打赏者用户 ID */
    private Long fromUserId;

    /** 接收主播用户 ID */
    private Long anchorId;

    /** 礼物 ID */
    private Long giftId;

    /** 赠送数量 */
    private Integer giftCount;

    private LocalDateTime createTime;
}
