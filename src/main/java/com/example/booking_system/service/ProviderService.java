package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.entity.Provider;
import com.example.booking_system.entity.User;
import com.example.booking_system.mapper.ProviderMapper;
import com.example.booking_system.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private final ProviderMapper providerMapper;
    private final UserMapper userMapper;

    public ProviderService(ProviderMapper providerMapper, UserMapper userMapper) {
        this.providerMapper = providerMapper;
        this.userMapper = userMapper;
    }

    public Map<String, Object> register(Integer userId, String companyName, String category) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (providerMapper.selectOne(new LambdaQueryWrapper<Provider>().eq(Provider::getUserId, userId)) != null) {
            throw new RuntimeException("该用户已是服务提供者");
        }
        Provider provider = new Provider();
        provider.setUserId(userId);
        provider.setCompanyName(companyName);
        provider.setCategory(category);
        provider.setRating(java.math.BigDecimal.valueOf(5.00));
        provider.setStatus(0);
        providerMapper.insert(provider);

        Map<String, Object> result = new HashMap<>();
        result.put("providerId", provider.getProviderId());
        result.put("companyName", provider.getCompanyName());
        return result;
    }

    public Map<String, Object> getProfile(Integer providerId) {
        Provider provider = providerMapper.selectById(providerId);
        if (provider == null) {
            throw new RuntimeException("服务商不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("providerId", provider.getProviderId());
        result.put("companyName", provider.getCompanyName());
        result.put("category", provider.getCategory());
        result.put("rating", provider.getRating());
        result.put("status", provider.getStatus());
        return result;
    }

    public Map<String, Object> getByUserId(Integer userId) {
        Provider provider = providerMapper.selectOne(
                new LambdaQueryWrapper<Provider>().eq(Provider::getUserId, userId));
        if (provider == null) return null;
        Map<String, Object> result = new HashMap<>();
        result.put("providerId", provider.getProviderId());
        result.put("companyName", provider.getCompanyName());
        result.put("category", provider.getCategory());
        result.put("rating", provider.getRating());
        result.put("status", provider.getStatus());
        return result;
    }

    public List<Map<String, Object>> listAll() {
        return providerMapper.selectList(null).stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("providerId", p.getProviderId());
            m.put("companyName", p.getCompanyName());
            m.put("category", p.getCategory());
            m.put("rating", p.getRating());
            m.put("status", p.getStatus());
            User u = userMapper.selectById(p.getUserId());
            m.put("username", u != null ? u.getUsername() : "未知");
            return m;
        }).collect(Collectors.toList());
    }

    public void audit(Integer providerId, Integer status) {
        Provider provider = providerMapper.selectById(providerId);
        if (provider == null) throw new RuntimeException("服务商不存在");
        provider.setStatus(status);
        providerMapper.updateById(provider);
        if (status == 1) {
            User user = userMapper.selectById(provider.getUserId());
            if (user != null) {
                user.setRole("PROVIDER");
                userMapper.updateById(user);
            }
        }
    }

    public void delete(Integer providerId) {
        Provider provider = providerMapper.selectById(providerId);
        if (provider == null) throw new RuntimeException("服务商不存在");
        providerMapper.deleteById(providerId);
        User user = userMapper.selectById(provider.getUserId());
        if (user != null) {
            user.setRole("USER");
            userMapper.updateById(user);
        }
    }
}
