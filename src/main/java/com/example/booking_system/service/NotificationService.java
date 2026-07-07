package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.entity.Notification;
import com.example.booking_system.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public void send(Integer userId, String title, String body) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setBody(body);
        n.setIsRead(0);
        notificationMapper.insert(n);
        System.out.println("[通知] 用户" + userId + " — " + title + ": " + body);
    }

    public List<Map<String, Object>> getMyNotifications(Integer userId) {
        return notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreatedAt)
        ).stream().map(n -> {
            Map<String, Object> m = new HashMap<>();
            m.put("notificationId", n.getNotificationId());
            m.put("title", n.getTitle());
            m.put("body", n.getBody());
            m.put("isRead", n.getIsRead());
            m.put("createdAt", n.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
    }

    public void markAsRead(Integer notificationId) {
        Notification n = notificationMapper.selectById(notificationId);
        if (n != null) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
    }
}
