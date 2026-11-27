package com.gamelive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.dto.HistoryItem;

public interface WatchHistoryService {

    /** 记录 / 更新观看历史 */
    void record(Long roomId, Long userId);

    /** 分页查询当前用户的观看历史（含直播间信息） */
    IPage<HistoryItem> pageHistory(Long userId, long current, long size);
}
