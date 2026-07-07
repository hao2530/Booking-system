package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.entity.Order;
import com.example.booking_system.entity.ScheduleSlot;
import com.example.booking_system.mapper.OrderMapper;
import com.example.booking_system.mapper.ScheduleSlotMapper;
import com.example.booking_system.mapper.ServiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final ScheduleSlotMapper slotMapper;
    private final ServiceMapper serviceMapper;

    public OrderService(OrderMapper orderMapper, ScheduleSlotMapper slotMapper,
                        ServiceMapper serviceMapper) {
        this.orderMapper = orderMapper;
        this.slotMapper = slotMapper;
        this.serviceMapper = serviceMapper;
    }

    @Transactional
    public Map<String, Object> createBooking(Integer userId, Integer slotId) {
        ScheduleSlot slot = slotMapper.selectOne(
                new LambdaQueryWrapper<ScheduleSlot>()
                        .eq(ScheduleSlot::getSlotId, slotId)
                        .last("FOR UPDATE")
        );
        if (slot == null) {
            throw new RuntimeException("时间段不存在");
        }
        if (slot.getIsAvailable() != 1) {
            throw new RuntimeException("该时间段已被预约");
        }
        if (slot.getSlotDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("不能预约过去的时间");
        }

        slot.setIsAvailable(0);
        slotMapper.updateById(slot);

        com.example.booking_system.entity.Service service = serviceMapper.selectById(slot.getServiceId());

        Order order = new Order();
        order.setUserId(userId);
        order.setSlotId(slotId);
        order.setTotalAmount(service.getPrice());
        order.setStatus("CONFIRMED");
        orderMapper.insert(order);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("status", order.getStatus());
        result.put("totalAmount", order.getTotalAmount());
        return result;
    }

    @Transactional
    public void cancelBooking(Integer orderId) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderId, orderId)
                        .last("FOR UPDATE")
        );
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new RuntimeException("当前订单状态不可取消");
        }

        order.setStatus("CANCELLED");
        orderMapper.updateById(order);

        ScheduleSlot slot = slotMapper.selectById(order.getSlotId());
        if (slot != null) {
            slot.setIsAvailable(1);
            slotMapper.updateById(slot);
        }
    }

    public List<Map<String, Object>> getUserBookings(Integer userId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId)
        ).stream().map(o -> {
            Map<String, Object> m = new HashMap<>();
            m.put("orderId", o.getOrderId());
            m.put("slotId", o.getSlotId());
            m.put("totalAmount", o.getTotalAmount());
            m.put("status", o.getStatus());
            m.put("createTime", o.getCreateTime());
            return m;
        }).collect(Collectors.toList());
    }
}
