package com.gamelive.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelive.config.SpringContextUtil;
import com.gamelive.dto.DanmuMessage;
import com.gamelive.entity.Danmu;
import com.gamelive.service.DanmuService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 直播间弹幕 WebSocket 端点。
 * 连接地址：ws://<host>/api/ws/danmu/{roomId}
 */
@Slf4j
@ServerEndpoint("/ws/danmu/{roomId}")
public class DanmuWebSocket {

    /** roomId -> 该房间在线会话集合 */
    private static final ConcurrentHashMap<String, Set<Session>> ROOM_SESSIONS = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        ROOM_SESSIONS.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("房间 {} 有观众进入，当前在线连接数：{}", roomId, ROOM_SESSIONS.get(roomId).size());
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("roomId") String roomId, String message) {
        try {
            DanmuMessage danmuMessage = OBJECT_MAPPER.readValue(message, DanmuMessage.class);
            danmuMessage.setRoomId(Long.valueOf(roomId));

            // 持久化弹幕记录
            DanmuService danmuService = SpringContextUtil.getBean(DanmuService.class);
            Danmu danmu = new Danmu();
            danmu.setRoomId(danmuMessage.getRoomId());
            danmu.setUserId(danmuMessage.getUserId());
            danmu.setContent(danmuMessage.getContent());
            danmuService.save(danmu);

            broadcast(roomId, OBJECT_MAPPER.writeValueAsString(danmuMessage));
        } catch (Exception e) {
            log.error("弹幕消息处理失败", e);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") String roomId) {
        Set<Session> sessions = ROOM_SESSIONS.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                ROOM_SESSIONS.remove(roomId);
            }
        }
        log.info("房间 {} 有观众离开", roomId);
    }

    /** 向房间内所有在线会话广播消息 */
    private void broadcast(String roomId, String json) {
        Set<Session> sessions = ROOM_SESSIONS.get(roomId);
        if (sessions == null) {
            return;
        }
        sessions.forEach(s -> {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(json);
                } catch (Exception e) {
                    log.error("弹幕推送失败", e);
                }
            }
        });
    }
}
