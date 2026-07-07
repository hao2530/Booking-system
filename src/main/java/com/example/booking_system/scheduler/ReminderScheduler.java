package com.example.booking_system.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.entity.Order;
import com.example.booking_system.entity.ScheduleSlot;
import com.example.booking_system.entity.Service;
import com.example.booking_system.mapper.OrderMapper;
import com.example.booking_system.mapper.ScheduleSlotMapper;
import com.example.booking_system.mapper.ServiceMapper;
import com.example.booking_system.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private final OrderMapper orderMapper;
    private final ScheduleSlotMapper slotMapper;
    private final ServiceMapper serviceMapper;
    private final NotificationService notificationService;

    public ReminderScheduler(OrderMapper orderMapper, ScheduleSlotMapper slotMapper,
                             ServiceMapper serviceMapper, NotificationService notificationService) {
        this.orderMapper = orderMapper;
        this.slotMapper = slotMapper;
        this.serviceMapper = serviceMapper;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusHours(1);

        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, "CONFIRMED")
        );

        for (Order order : orders) {
            ScheduleSlot slot = slotMapper.selectById(order.getSlotId());
            if (slot == null) continue;

            LocalDateTime slotDateTime = slot.getSlotDate().atTime(
                    LocalTime.parse(slot.getStartTime())
            );

            if (slotDateTime.isAfter(now) && slotDateTime.isBefore(reminderTime)) {
                Service service = serviceMapper.selectById(slot.getServiceId());
                if (service != null) {
                    notificationService.send(order.getUserId(), "预约提醒",
                            "您预约的「" + service.getTitle() + "」将在1小时后开始，请准时前往。");
                }
            }
        }
    }
}