package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 弹幕消息。
 */
@Data
@TableName("danmu")
public class Danmu {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 直播间 ID */
    private Long roomId;

    /** 发送者用户 ID */
    private Long userId;

    /** 弹幕内容 */
    private String content;

    private LocalDateTime createTime;
}
