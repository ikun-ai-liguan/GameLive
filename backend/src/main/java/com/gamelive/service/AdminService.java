package com.gamelive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.entity.LiveRoom;
import com.gamelive.entity.User;

import java.util.Map;

public interface AdminService {

    /** 分页查询用户 */
    IPage<User> pageUsers(long current, long size, String keyword);

    /** 封禁 / 解封用户 */
    void banUser(Long userId, boolean banned, String reason);

    /** 分页查询直播间 */
    IPage<LiveRoom> pageRooms(long current, long size, String keyword);

    /** 直播间审核 */
    void auditRoom(Long roomId, boolean approved, String reason);

    /** 基础数据统计 */
    Map<String, Object> statistics();
}
