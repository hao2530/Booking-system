package com.example.booking_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booking_system.common.JwtUtil;
import com.example.booking_system.entity.User;
import com.example.booking_system.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String adminSecretKey = "Admin888";

    public UserService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> register(String username, String password, String phone, String email, String role, String adminKey) {
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (email != null && !email.isEmpty() && userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        if (phone != null && !phone.isEmpty() && userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) != null) {
            throw new RuntimeException("手机号已被注册");
        }
        if (role == null || role.isEmpty()) role = "USER";
        if ("ADMIN".equals(role) && !adminSecretKey.equals(adminKey)) {
            throw new RuntimeException("管理员密钥错误");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setPhone(phone);
        user.setEmail(email);
        user.setRole(role);
        userMapper.insert(user);
        String token = jwtUtil.generateToken(user.getUserId(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        return result;
    }

    public Map<String, Object> login(String username, String password, String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUsername, username);
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        User user = userMapper.selectOne(wrapper);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getUserId(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        return result;
    }
}
