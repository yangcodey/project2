package com.example.project2.controller;

import com.example.project2.entity.Notification;
import com.example.project2.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取用户通知列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(
                userId, status, (page - 1) * size, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取通知成功");
            response.put("data", notifications);
            response.put("page", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取通知失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestParam Long userId) {
        try {
            Integer count = notificationService.getUnreadCount(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取未读数量成功");
            response.put("data", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取未读数量失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 创建通知
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotification(@RequestBody Notification notification) {
        try {
            Notification createdNotification = notificationService.createNotification(notification);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "通知创建成功");
            response.put("data", createdNotification);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "通知创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "标记已读成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "标记已读失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/mark-all-read")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@RequestParam Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "全部标记已读成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "全部标记已读失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除通知成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "删除通知失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取通知统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats(@RequestParam Long userId) {
        try {
            Map<String, Object> stats = notificationService.getNotificationStats(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取通知统计成功");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取通知统计失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 创建订单通知
     */
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> createOrderNotification(
            @RequestParam Long userId,
            @RequestParam String orderNo,
            @RequestParam String status) {
        try {
            notificationService.createOrderNotification(userId, orderNo, status);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "订单通知创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "订单通知创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 创建库存预警通知
     */
    @PostMapping("/stock")
    public ResponseEntity<Map<String, Object>> createStockNotification(
            @RequestParam Long userId,
            @RequestParam String productName,
            @RequestParam Integer stock) {
        try {
            notificationService.createStockNotification(userId, productName, stock);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "库存通知创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "库存通知创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
