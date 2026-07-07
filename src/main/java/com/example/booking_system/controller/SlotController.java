package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.SlotService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/generate")
    public R<List<Map<String, Object>>> generate(@RequestBody Map<String, String> req) {
        return R.ok(slotService.generateSlots(
                Integer.parseInt(req.get("serviceId")),
                LocalDate.parse(req.get("date")),
                LocalTime.parse(req.get("startTime")),
                LocalTime.parse(req.get("endTime"))
        ));
    }

    @GetMapping
    public R<List<Map<String, Object>>> getAvailable(
            @RequestParam Integer serviceId,
            @RequestParam String date) {
        return R.ok(slotService.getAvailableSlots(serviceId, LocalDate.parse(date)));
    }
}
