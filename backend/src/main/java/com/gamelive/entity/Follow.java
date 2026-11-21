package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关注关系：观众关注主播。
 */
@Data
@TableName("follow")
public class Follow {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关注者（观众）用户 ID */
    private Long userId;

    /** 被关注者（主播）用户 ID */
    private Long anchorId;

    private LocalDateTime createTime;
}
