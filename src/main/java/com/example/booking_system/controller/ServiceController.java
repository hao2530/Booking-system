package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.ServiceService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public R<Map<String, Object>> create(@RequestBody Map<String, Object> req) {
        return R.ok(serviceService.create(
                (Integer) req.get("providerId"),
                (String) req.get("title"),
                (String) req.get("description"),
                new BigDecimal(req.get("price").toString()),
                (Integer) req.get("durationMin"),
                (String) req.get("category")
        ));
    }

    @PutMapping("/{id}")
    public R<Map<String, Object>> update(@PathVariable Integer id, @RequestBody Map<String, Object> req) {
        return R.ok(serviceService.update(
                id,
                (String) req.get("title"),
                (String) req.get("description"),
                req.get("price") != null ? new BigDecimal(req.get("price").toString()) : null,
                (Integer) req.get("durationMin"),
                (String) req.get("category")
        ));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        serviceService.delete(id);
        return R.ok();
    }

    @GetMapping("/by-provider/{providerId}")
    public R<List<Map<String, Object>>> listByProvider(@PathVariable Integer providerId) {
        return R.ok(serviceService.listByProvider(providerId));
    }

    @GetMapping("/search")
    public R<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(serviceService.search(keyword, category, sort, page, size));
    }
}
