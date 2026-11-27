package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户举报记录。
 */
@Data
@TableName("report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 举报人用户 ID */
    private Long userId;

    /** 目标类型：ROOM-直播间，DANMU-弹幕 */
    private String targetType;

    /** 目标 ID（直播间 ID 或弹幕 ID） */
    private Long targetId;

    /** 举报原因 */
    private String reason;

    /** 处理状态：PENDING-待处理，RESOLVED-已处理，REJECTED-驳回 */
    private String status;

    private LocalDateTime createTime;
}
