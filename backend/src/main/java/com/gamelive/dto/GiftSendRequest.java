package com.gamelive.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GiftSendRequest {

    @NotNull(message = "直播间 ID 不能为空")
    private Long roomId;

    @NotNull(message = "礼物 ID 不能为空")
    private Long giftId;

    @NotNull(message = "赠送数量不能为空")
    private Integer giftCount;
}
