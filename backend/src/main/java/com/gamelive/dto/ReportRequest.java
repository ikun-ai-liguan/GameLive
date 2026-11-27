package com.gamelive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 举报请求。
 */
@Data
public class ReportRequest {

    /** ROOM-直播间，DANMU-弹幕 */
    @NotBlank(message = "举报类型不能为空")
    private String targetType;

    @NotNull(message = "举报目标 ID 不能为空")
    private Long targetId;

    @NotBlank(message = "举报原因不能为空")
    private String reason;
}
