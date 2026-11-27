package com.gamelive.service;

public interface LikeService {

    /** 点赞（同一用户对同一直播间只记录一次） */
    void like(Long roomId, Long userId);

    /** 当前用户是否已点赞 */
    boolean liked(Long roomId, Long userId);
}
