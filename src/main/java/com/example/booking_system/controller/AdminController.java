package com.example.booking_system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.common.R;
import com.example.booking_system.entity.ScheduleSlot;
import com.example.booking_system.mapper.ScheduleSlotMapper;
import com.example.booking_system.service.ProviderService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProviderService providerService;
    private final ScheduleSlotMapper slotMapper;

    public AdminController(ProviderService providerService, ScheduleSlotMapper slotMapper) {
        this.providerService = providerService;
        this.slotMapper = slotMapper;
    }

    @GetMapping("/providers")
    public R<List<Map<String, Object>>> listProviders() {
        return R.ok(providerService.listAll());
    }

    @PutMapping("/providers/{id}/audit")
    public R<Void> auditProvider(@PathVariable Integer id, @RequestParam Integer status) {
        providerService.audit(id, status);
        return R.ok();
    }

    @GetMapping("/slots")
    public R<List<Map<String, Object>>> listSlots() {
        return R.ok(slotMapper.selectList(
                new LambdaQueryWrapper<ScheduleSlot>().orderByDesc(ScheduleSlot::getSlotDate)
        ).stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("slotId", s.getSlotId());
            m.put("serviceId", s.getServiceId());
            m.put("date", s.getSlotDate().toString());
            m.put("startTime", s.getStartTime().toString());
            m.put("endTime", s.getEndTime().toString());
            m.put("isAvailable", s.getIsAvailable());
            return m;
        }).collect(Collectors.toList()));
    }

    @DeleteMapping("/slots/{id}")
    public R<Void> deleteSlot(@PathVariable Integer id) {
        slotMapper.deleteById(id);
        return R.ok();
    }
}
