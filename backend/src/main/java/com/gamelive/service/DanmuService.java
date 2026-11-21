package com.gamelive.service;

import com.gamelive.entity.Danmu;

import java.util.List;

public interface DanmuService {

    /** 保存弹幕记录 */
    void save(Danmu danmu);

    /** 查询房间历史弹幕 */
    List<Danmu> listByRoom(Long roomId);
}
