package com.example.booking_system.controller;

import com.example.booking_system.common.R;
import com.example.booking_system.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public R<Void> add(@RequestBody Map<String, Integer> req) {
        favoriteService.add(req.get("userId"), req.get("serviceId"));
        return R.ok();
    }

    @DeleteMapping
    public R<Void> remove(@RequestBody Map<String, Integer> req) {
        favoriteService.remove(req.get("userId"), req.get("serviceId"));
        return R.ok();
    }

    @GetMapping("/check")
    public R<Map<String, Boolean>> check(@RequestParam Integer userId, @RequestParam Integer serviceId) {
        Map<String, Boolean> result = Map.of("isFavorite", favoriteService.isFavorite(userId, serviceId));
        return R.ok(result);
    }

    @GetMapping("/my")
    public R<List<Map<String, Object>>> myFavorites(@RequestParam Integer userId) {
        return R.ok(favoriteService.listByUser(userId));
    }
}