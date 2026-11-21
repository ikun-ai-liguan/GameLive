package com.gamelive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.dto.RoomCreateRequest;
import com.gamelive.entity.LiveRoom;

import java.util.List;

public interface LiveRoomService {

    /** 主播创建直播间，自动生成推流密钥 */
    LiveRoom createRoom(Long anchorId, RoomCreateRequest request);

    /** 分页查询直播间，支持分类筛选与标题关键词搜索 */
    IPage<LiveRoom> pageRooms(Long categoryId, String keyword, long current, long size);

    /** 查询直播间详情 */
    LiveRoom getRoom(Long roomId);

    /** 开播 */
    void startLive(Long roomId);

    /** 下播 */
    void stopLive(Long roomId);

    /** 回放列表 */
    List<LiveRoom> listReplays();
}
