package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 禁言 / 封禁记录。
 */
@Data
@TableName("ban_record")
public class BanRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 直播间 ID，为空表示平台级封禁 */
    private Long roomId;

    /** 操作人 ID（主播 / 管理员） */
    private Long operatorId;

    /** 被处罚用户 ID */
    private Long targetUserId;

    /** 处罚类型：MUTE-禁言，BAN-封禁 */
    private String type;

    /** 处罚原因 */
    private String reason;

    /** 解封 / 解禁时间，为空表示永久 */
    private LocalDateTime expireTime;

    private LocalDateTime createTime;
}
