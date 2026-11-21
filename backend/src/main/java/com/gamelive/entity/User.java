package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户：普通观众 / 主播 / 管理员。
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 密码（数据库存储加密后的摘要） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 个性签名 */
    private String signature;

    /** 角色：USER-观众，ANCHOR-主播，ADMIN-管理员 */
    private String role;

    /** 状态：NORMAL-正常，BANNED-封禁 */
    private String status;

    /** 粉丝数（冗余统计字段） */
    private Integer fansCount;

    /** 关注数（冗余统计字段） */
    private Integer followCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
