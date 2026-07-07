package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booking_system.mapper.ServiceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

    public Map<String, Object> create(Integer providerId, String title, String description,
                                       BigDecimal price, Integer durationMin, String category) {
        com.example.booking_system.entity.Service service = new com.example.booking_system.entity.Service();
        service.setProviderId(providerId);
        service.setTitle(title);
        service.setDescription(description);
        service.setPrice(price);
        service.setDurationMin(durationMin);
        service.setCategory(category);
        serviceMapper.insert(service);

        Map<String, Object> result = new HashMap<>();
        result.put("serviceId", service.getServiceId());
        result.put("title", service.getTitle());
        return result;
    }

    public Map<String, Object> update(Integer serviceId, String title, String description,
                                       BigDecimal price, Integer durationMin, String category) {
        com.example.booking_system.entity.Service service = serviceMapper.selectById(serviceId);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        if (title != null) service.setTitle(title);
        if (description != null) service.setDescription(description);
        if (price != null) service.setPrice(price);
        if (durationMin != null) service.setDurationMin(durationMin);
        if (category != null) service.setCategory(category);
        serviceMapper.updateById(service);

        Map<String, Object> result = new HashMap<>();
        result.put("serviceId", service.getServiceId());
        result.put("title", service.getTitle());
        return result;
    }

    public void delete(Integer serviceId) {
        if (serviceMapper.selectById(serviceId) == null) {
            throw new RuntimeException("服务不存在");
        }
        serviceMapper.deleteById(serviceId);
    }

    public List<Map<String, Object>> listByProvider(Integer providerId) {
        return serviceMapper.selectList(
                new LambdaQueryWrapper<com.example.booking_system.entity.Service>()
                        .eq(com.example.booking_system.entity.Service::getProviderId, providerId)
        ).stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("serviceId", s.getServiceId());
            m.put("title", s.getTitle());
            m.put("price", s.getPrice());
            m.put("durationMin", s.getDurationMin());
            m.put("category", s.getCategory());
            return m;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> search(String keyword, String category, Integer page, Integer size) {
        LambdaQueryWrapper<com.example.booking_system.entity.Service> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(com.example.booking_system.entity.Service::getTitle, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(com.example.booking_system.entity.Service::getCategory, category);
        }
        Page<com.example.booking_system.entity.Service> p =
                serviceMapper.selectPage(new Page<>(page, size), wrapper);

        List<Map<String, Object>> list = p.getRecords().stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("serviceId", s.getServiceId());
            m.put("providerId", s.getProviderId());
            m.put("title", s.getTitle());
            m.put("price", s.getPrice());
            m.put("durationMin", s.getDurationMin());
            m.put("category", s.getCategory());
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", p.getTotal());
        result.put("page", p.getCurrent());
        result.put("pages", p.getPages());
        return result;
    }
}
