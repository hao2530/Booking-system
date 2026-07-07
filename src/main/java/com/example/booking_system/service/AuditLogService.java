package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booking_system.entity.AuditLog;
import com.example.booking_system.mapper.AuditLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;

    public AuditLogService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public void log(int operatorId, String action, String targetType, Integer targetId,
                    String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setOperatorId(operatorId);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setStatus("PENDING");
        auditLogMapper.insert(log);
    }

    public Page<AuditLog> list(int page, int size, String status) {
        Page<AuditLog> p = new Page<>(page, size);
        LambdaQueryWrapper<AuditLog> q = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            q.eq(AuditLog::getStatus, status);
        }
        q.orderByDesc(AuditLog::getCreatedAt);
        return auditLogMapper.selectPage(p, q);
    }

    public void review(int logId, int reviewerId, String status, String remark) {
        AuditLog log = auditLogMapper.selectById(logId);
        if (log == null) throw new RuntimeException("审计记录不存在");
        log.setStatus(status);
        log.setRemark(remark);
        log.setReviewerId(reviewerId);
        log.setReviewedAt(LocalDateTime.now());
        auditLogMapper.updateById(log);
    }
}
