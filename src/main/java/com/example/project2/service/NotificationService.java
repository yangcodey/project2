package com.example.project2.service;

import com.example.project2.entity.Notification;
import com.example.project2.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 获取用户通知列表
     */
    public List<Notification> getUserNotifications(Long userId, String status, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        return notificationMapper.getUserNotifications(userId, status, offset, size);
    }

    /**
     * 获取未读通知数量
     */
    public Integer getUnreadCount(Long userId) {
        return notificationMapper.getUnreadCount(userId);
    }

    /**
     * 创建通知
     */
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus("UNREAD");
        if (notification.getPriority() == null) {
            notification.setPriority("NORMAL");
        }
        notificationMapper.insertNotification(notification);
        return notification;
    }

    /**
     * 标记通知为已读
     */
    public void markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId, LocalDateTime.now());
    }

    /**
     * 标记所有通知为已读
     */
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId, LocalDateTime.now());
    }

    /**
     * 删除通知
     */
    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteNotification(notificationId);
    }

    /**
     * 删除过期通知
     */
    public void deleteExpiredNotifications() {
        notificationMapper.deleteExpiredNotifications(LocalDateTime.now());
    }

    /**
     * 创建订单通知
     */
    public void createOrderNotification(Long userId, String orderNo, String status) {
        String title = "订单状态更新";
        String content = String.format("您的订单 %s 状态已更新为：%s", orderNo, getOrderStatusText(status));
        String actionUrl = "/orders";
        String actionText = "查看订单";
        String priority = getOrderPriority(status);

        Notification notification = new Notification(userId, title, content, "ORDER");
        notification.setActionUrl(actionUrl);
        notification.setActionText(actionText);
        notification.setPriority(priority);
        
        createNotification(notification);
    }

    /**
     * 创建库存预警通知
     */
    public void createStockNotification(Long userId, String productName, Integer stock) {
        String title = "库存预警";
        String content = String.format("产品 %s 库存不足，当前库存：%d", productName, stock);
        String actionUrl = "/seller-products";
        String actionText = "管理产品";

        Notification notification = new Notification(userId, title, content, "STOCK");
        notification.setActionUrl(actionUrl);
        notification.setActionText(actionText);
        notification.setPriority("HIGH");
        
        createNotification(notification);
    }

    /**
     * 创建系统通知
     */
    public void createSystemNotification(Long userId, String title, String content) {
        Notification notification = new Notification(userId, title, content, "SYSTEM");
        notification.setPriority("NORMAL");
        createNotification(notification);
    }

    /**
     * 创建促销通知
     */
    public void createPromotionNotification(Long userId, String title, String content) {
        Notification notification = new Notification(userId, title, content, "PROMOTION");
        notification.setPriority("LOW");
        notification.setActionUrl("/products");
        notification.setActionText("查看商品");
        createNotification(notification);
    }

    /**
     * 获取通知统计
     */
    public Map<String, Object> getNotificationStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", notificationMapper.getTotalCount(userId));
        stats.put("unread", notificationMapper.getUnreadCount(userId));
        stats.put("order", notificationMapper.getCountByType(userId, "ORDER"));
        stats.put("system", notificationMapper.getCountByType(userId, "SYSTEM"));
        stats.put("stock", notificationMapper.getCountByType(userId, "STOCK"));
        stats.put("promotion", notificationMapper.getCountByType(userId, "PROMOTION"));
        return stats;
    }

    private String getOrderStatusText(String status) {
        switch (status) {
            case "PENDING_PAYMENT": return "待支付";
            case "PAID": return "已支付";
            case "SHIPPED": return "已发货";
            case "DELIVERED": return "已送达";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }

    private String getOrderPriority(String status) {
        switch (status) {
            case "PENDING_PAYMENT": return "HIGH";
            case "PAID": return "NORMAL";
            case "SHIPPED": return "NORMAL";
            case "DELIVERED": return "NORMAL";
            case "COMPLETED": return "LOW";
            case "CANCELLED": return "HIGH";
            default: return "NORMAL";
        }
    }
}
