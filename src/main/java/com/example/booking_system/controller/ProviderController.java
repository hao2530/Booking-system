package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.ProviderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping("/register")
    public R<Map<String, Object>> register(@RequestBody Map<String, Object> req) {
        return R.ok(providerService.register(
                (Integer) req.get("userId"),
                (String) req.get("companyName"),
                (String) req.get("category")
        ));
    }

    @GetMapping("/{id}")
    public R<Map<String, Object>> getProfile(@PathVariable Integer id) {
        return R.ok(providerService.getProfile(id));
    }

    @GetMapping("/by-user/{userId}")
    public R<Map<String, Object>> getByUserId(@PathVariable Integer userId) {
        Map<String, Object> result = providerService.getByUserId(userId);
        if (result == null) return R.error("尚未注册为服务商");
        return R.ok(result);
    }
}
