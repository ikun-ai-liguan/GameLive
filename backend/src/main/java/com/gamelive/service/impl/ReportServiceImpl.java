package com.gamelive.service.impl;

import com.gamelive.common.BusinessException;
import com.gamelive.dto.ReportRequest;
import com.gamelive.entity.Report;
import com.gamelive.mapper.DanmuMapper;
import com.gamelive.mapper.LiveRoomMapper;
import com.gamelive.mapper.ReportMapper;
import com.gamelive.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final Set<String> TARGET_TYPES = Set.of("ROOM", "DANMU");

    private final ReportMapper reportMapper;
    private final LiveRoomMapper liveRoomMapper;
    private final DanmuMapper danmuMapper;

    @Override
    public void create(Long userId, ReportRequest request) {
        String targetType = request.getTargetType().trim().toUpperCase();
        if (!TARGET_TYPES.contains(targetType)) {
            throw new BusinessException("举报类型仅支持 ROOM 或 DANMU");
        }
        if ("ROOM".equals(targetType) && liveRoomMapper.selectById(request.getTargetId()) == null) {
            throw new BusinessException("直播间不存在");
        }
        if ("DANMU".equals(targetType) && danmuMapper.selectById(request.getTargetId()) == null) {
            throw new BusinessException("弹幕不存在");
        }
        Report report = new Report();
        report.setUserId(userId);
        report.setTargetType(targetType);
        report.setTargetId(request.getTargetId());
        report.setReason(request.getReason());
        report.setStatus("PENDING");
        report.setCreateTime(LocalDateTime.now());
        reportMapper.insert(report);
    }
}
