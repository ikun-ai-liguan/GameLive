package com.gamelive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gamelive.entity.Notification;

public interface NotificationService {

    /** 分页查询当前用户的通知（含已读和未读） */
    IPage<Notification> pageNotifications(Long userId, long current, long size);

    /** 将通知标记为已读 */
    void markRead(Long notificationId, Long userId);
}
