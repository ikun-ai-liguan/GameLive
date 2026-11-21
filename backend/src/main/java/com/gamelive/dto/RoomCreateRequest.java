package com.gamelive.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomCreateRequest {

    @NotBlank(message = "直播间标题不能为空")
    private String title;

    private Long categoryId;

    private String cover;

    private String notice;
}
