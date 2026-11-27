package com.gamelive.service;

import com.gamelive.dto.ReportRequest;

public interface ReportService {

    /** 提交举报 */
    void create(Long userId, ReportRequest request);
}
