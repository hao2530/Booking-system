package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public R<Map<String, Object>> submit(@RequestBody Map<String, Object> req) {
        return R.ok(reviewService.submitReview(
                (Integer) req.get("userId"),
                (Integer) req.get("orderId"),
                (Integer) req.get("rating"),
                (String) req.get("comment")
        ));
    }

    @GetMapping("/by-service/{serviceId}")
    public R<List<Map<String, Object>>> getByService(@PathVariable Integer serviceId) {
        return R.ok(reviewService.getServiceReviews(serviceId));
    }
}
