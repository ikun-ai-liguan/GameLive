package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.config.AuthContext;
import com.gamelive.dto.ReportRequest;
import com.gamelive.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 举报。
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ReportRequest request) {
        reportService.create(AuthContext.getUserId(), request);
        return Result.success();
    }
}
