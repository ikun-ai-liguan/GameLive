package com.gamelive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 礼物字典。
 */
@Data
@TableName("gift")
public class Gift {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 礼物名称 */
    private String name;

    /** 礼物图标 */
    private String icon;

    /** 单价（虚拟币 / 元，按业务定义） */
    private BigDecimal price;

    /** 排序值 */
    private Integer sort;
}
