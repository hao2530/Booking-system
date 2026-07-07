package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.entity.Favorite;
import com.example.booking_system.entity.Service;
import com.example.booking_system.mapper.FavoriteMapper;
import com.example.booking_system.mapper.ServiceMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ServiceMapper serviceMapper;

    public FavoriteService(FavoriteMapper favoriteMapper, ServiceMapper serviceMapper) {
        this.favoriteMapper = favoriteMapper;
        this.serviceMapper = serviceMapper;
    }

    public void add(Integer userId, Integer serviceId) {
        Favorite existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getServiceId, serviceId)
        );
        if (existing != null) {
            throw new RuntimeException("已收藏该服务");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setServiceId(serviceId);
        favoriteMapper.insert(favorite);
    }

    public void remove(Integer userId, Integer serviceId) {
        favoriteMapper.delete(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getServiceId, serviceId)
        );
    }

    public boolean isFavorite(Integer userId, Integer serviceId) {
        return favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getServiceId, serviceId)
        ) > 0;
    }

    public List<Map<String, Object>> listByUser(Integer userId) {
        List<Favorite> favorites = favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime)
        );
        
        Set<Integer> serviceIds = favorites.stream()
                .map(Favorite::getServiceId)
                .collect(Collectors.toSet());
        
        List<Service> services = serviceMapper.selectBatchIds(serviceIds);
        
        return favorites.stream().map(f -> {
            Service s = services.stream()
                    .filter(service -> service.getServiceId().equals(f.getServiceId()))
                    .findFirst()
                    .orElse(null);
            
            Map<String, Object> m = new HashMap<>();
            if (s != null) {
                m.put("serviceId", s.getServiceId());
                m.put("title", s.getTitle());
                m.put("price", s.getPrice());
                m.put("durationMin", s.getDurationMin());
                m.put("category", s.getCategory());
            }
            return m;
        }).collect(Collectors.toList());
    }
}