package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.entity.Order;
import com.example.booking_system.entity.Provider;
import com.example.booking_system.entity.Review;
import com.example.booking_system.entity.ScheduleSlot;
import com.example.booking_system.mapper.OrderMapper;
import com.example.booking_system.mapper.ProviderMapper;
import com.example.booking_system.mapper.ReviewMapper;
import com.example.booking_system.mapper.ScheduleSlotMapper;
import com.example.booking_system.mapper.ServiceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final ScheduleSlotMapper slotMapper;
    private final ServiceMapper serviceMapper;
    private final ProviderMapper providerMapper;

    public ReviewService(ReviewMapper reviewMapper, OrderMapper orderMapper,
                         ScheduleSlotMapper slotMapper, ServiceMapper serviceMapper,
                         ProviderMapper providerMapper) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.slotMapper = slotMapper;
        this.serviceMapper = serviceMapper;
        this.providerMapper = providerMapper;
    }

    public Map<String, Object> submitReview(Integer userId, Integer orderId, Integer rating, String comment) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new RuntimeException("只能评价自己的订单");
        if (!"CONFIRMED".equals(order.getStatus())) throw new RuntimeException("只能评价已完成的订单");
        if (reviewMapper.selectOne(new LambdaQueryWrapper<Review>().eq(Review::getOrderId, orderId)) != null)
            throw new RuntimeException("该订单已评价");
        if (rating < 1 || rating > 5) throw new RuntimeException("评分范围为1-5");

        Review review = new Review();
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setRating(rating);
        review.setComment(comment);
        reviewMapper.insert(review);

        updateProviderRating(order.getSlotId());

        Map<String, Object> result = new HashMap<>();
        result.put("reviewId", review.getReviewId());
        result.put("rating", review.getRating());
        return result;
    }

    private void updateProviderRating(Integer slotId) {
        ScheduleSlot slot = slotMapper.selectById(slotId);
        if (slot == null) return;
        com.example.booking_system.entity.Service service = serviceMapper.selectById(slot.getServiceId());
        if (service == null) return;
        Provider provider = providerMapper.selectById(service.getProviderId());
        if (provider == null) return;

        List<Review> providerReviews = reviewMapper.selectList(null).stream()
                .filter(r -> {
                    Order o = orderMapper.selectById(r.getOrderId());
                    if (o == null) return false;
                    ScheduleSlot s = slotMapper.selectById(o.getSlotId());
                    if (s == null) return false;
                    com.example.booking_system.entity.Service sv = serviceMapper.selectById(s.getServiceId());
                    return sv != null && sv.getProviderId().equals(provider.getProviderId());
                })
                .collect(Collectors.toList());

        double avg = providerReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(5.0);
        provider.setRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        providerMapper.updateById(provider);
    }

    public List<Map<String, Object>> getServiceReviews(Integer serviceId) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>().orderByDesc(Review::getCreatedAt)
        ).stream().map(r -> {
            Order o = orderMapper.selectById(r.getOrderId());
            if (o == null) return null;
            ScheduleSlot s = slotMapper.selectById(o.getSlotId());
            if (s == null || !s.getServiceId().equals(serviceId)) return null;
            Map<String, Object> m = new HashMap<>();
            m.put("reviewId", r.getReviewId());
            m.put("rating", r.getRating());
            m.put("comment", r.getComment());
            m.put("createdAt", r.getCreatedAt());
            return m;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
