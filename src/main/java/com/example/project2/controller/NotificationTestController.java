package com.example.project2.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class NotificationTestController {

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Map<String, Object>> testUnreadCount(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "测试API工作正常");
        response.put("data", 3); // 模拟未读通知数量
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> testNotifications(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "测试API工作正常");
        response.put("data", new Object[]{
            Map.of("id", 1, "title", "测试通知1", "content", "这是一个测试通知", "status", "UNREAD"),
            Map.of("id", 2, "title", "测试通知2", "content", "这是另一个测试通知", "status", "READ")
        });
        return ResponseEntity.ok(response);
    }
}
