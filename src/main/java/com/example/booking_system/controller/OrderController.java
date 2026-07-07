package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public R<Map<String, Object>> create(@RequestBody Map<String, Object> req) {
        return R.ok(orderService.createBooking(
                (Integer) req.get("userId"),
                (Integer) req.get("slotId")
        ));
    }

    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Integer id) {
        orderService.cancelBooking(id);
        return R.ok();
    }

    @GetMapping("/my")
    public R<List<Map<String, Object>>> myBookings(@RequestParam Integer userId) {
        return R.ok(orderService.getUserBookings(userId));
    }

    @GetMapping("/provider/{providerId}")
    public R<List<Map<String, Object>>> providerBookings(@PathVariable Integer providerId) {
        return R.ok(orderService.getProviderBookings(providerId));
    }
}
