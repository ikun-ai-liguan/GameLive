package com.gamelive.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 观看历史条目（附带直播间信息）。
 */
@Data
public class HistoryItem {

    private Long roomId;

    private String title;

    private String cover;

    private Long anchorId;

    private String anchorNickname;

    private LocalDateTime watchedAt;
}
