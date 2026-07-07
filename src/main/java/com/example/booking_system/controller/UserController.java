package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public R<Map<String, Object>> register(@RequestBody Map<String, String> req) {
        return R.ok(userService.register(
                req.get("username"),
                req.get("password"),
                req.get("phone")
        ));
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> req) {
        return R.ok(userService.login(
                req.get("username"),
                req.get("password")
        ));
    }
}
