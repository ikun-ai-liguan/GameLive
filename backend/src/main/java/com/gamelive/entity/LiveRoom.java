package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播间。
 */
@Data
@TableName("live_room")
public class LiveRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 主播用户 ID */
    private Long anchorId;

    /** 直播间标题 */
    private String title;

    /** 游戏分类 ID */
    private Long categoryId;

    /** 封面图地址 */
    private String cover;

    /** 推流密钥，与主播 ID 拼接生成 RTMP 地址 */
    private String streamKey;

    /** 状态：NOT_LIVING-未开播，LIVING-直播中，REPLAY-回放 */
    private String status;

    /** 公告 / 简介 */
    private String notice;

    /** 当前在线人数（冗余统计字段） */
    private Integer viewerCount;

    /** 点赞数（冗余统计字段） */
    private Integer likeCount;

    /** 回放视频地址 */
    private String replayUrl;

    /** 审核状态：PENDING-待审核，APPROVED-通过，REJECTED-驳回 */
    private String auditStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
