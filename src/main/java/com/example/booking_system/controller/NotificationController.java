package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/my")
    public R<List<Map<String, Object>>> myNotifications(@RequestParam Integer userId) {
        return R.ok(notificationService.getMyNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public R<Void> markRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return R.ok();
    }
}
