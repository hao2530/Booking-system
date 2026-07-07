package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.entity.ScheduleSlot;
import com.example.booking_system.mapper.ScheduleSlotMapper;
import com.example.booking_system.mapper.ServiceMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SlotService {

    private final ScheduleSlotMapper slotMapper;
    private final ServiceMapper serviceMapper;

    public SlotService(ScheduleSlotMapper slotMapper, ServiceMapper serviceMapper) {
        this.slotMapper = slotMapper;
        this.serviceMapper = serviceMapper;
    }

    public List<Map<String, Object>> generateSlots(Integer serviceId, LocalDate date,
                                                    LocalTime startTime, LocalTime endTime) {
        com.example.booking_system.entity.Service service = serviceMapper.selectById(serviceId);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        int duration = service.getDurationMin();

        List<Map<String, Object>> result = new ArrayList<>();
        LocalTime current = startTime;

        while (current.isBefore(endTime)) {
            LocalTime slotEnd = current.plusMinutes(duration);
            if (slotEnd.isAfter(endTime)) break;

            ScheduleSlot existing = slotMapper.selectOne(
                    new LambdaQueryWrapper<ScheduleSlot>()
                            .eq(ScheduleSlot::getServiceId, serviceId)
                            .eq(ScheduleSlot::getSlotDate, date)
                            .eq(ScheduleSlot::getStartTime, current)
            );
            if (existing != null) {
                current = slotEnd;
                continue;
            }

            ScheduleSlot slot = new ScheduleSlot();
            slot.setServiceId(serviceId);
            slot.setSlotDate(date);
            slot.setStartTime(current);
            slot.setEndTime(slotEnd);
            slot.setIsAvailable(1);
            slotMapper.insert(slot);

            Map<String, Object> m = new HashMap<>();
            m.put("slotId", slot.getSlotId());
            m.put("startTime", current.toString());
            m.put("endTime", slotEnd.toString());
            result.add(m);

            current = slotEnd;
        }
        return result;
    }

    public List<Map<String, Object>> getAvailableSlots(Integer serviceId, LocalDate date) {
        return slotMapper.selectList(
                new LambdaQueryWrapper<ScheduleSlot>()
                        .eq(ScheduleSlot::getServiceId, serviceId)
                        .eq(ScheduleSlot::getSlotDate, date)
                        .eq(ScheduleSlot::getIsAvailable, 1)
        ).stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("slotId", s.getSlotId());
            m.put("startTime", s.getStartTime().toString());
            m.put("endTime", s.getEndTime().toString());
            return m;
        }).collect(Collectors.toList());
    }
}
