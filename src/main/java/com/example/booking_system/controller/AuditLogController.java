package com.example.booking_system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booking_system.common.R;
import com.example.booking_system.entity.AuditLog;
import com.example.booking_system.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/list")
    public R<Page<AuditLog>> list(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(required = false) String status) {
        return R.ok(auditLogService.list(page, size, status));
    }

    @PostMapping("/review")
    public R<Void> review(@RequestBody Map<String, Object> req) {
        int logId = (int) req.get("logId");
        int reviewerId = (int) req.get("reviewerId");
        String status = (String) req.get("status");
        String remark = (String) req.getOrDefault("remark", "");
        auditLogService.review(logId, reviewerId, status, remark);
        return R.ok();
    }
}
