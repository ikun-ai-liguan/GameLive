package com.gamelive.dto;

import lombok.Data;

/**
 * WebSocket 弹幕消息体。
 */
@Data
public class DanmuMessage {

    /** 消息类型：ENTER-进入房间，DANMU-弹幕，LIKE-点赞，GIFT-礼物，SYSTEM-系统通知 */
    private String type;

    private Long roomId;

    private Long userId;

    private String nickname;

    private String content;
}
