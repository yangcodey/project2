package com.example.project2.mapper;

import com.example.project2.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotificationMapper {

    /**
     * 获取用户通知列表
     */
    @Select("SELECT * FROM notifications WHERE user_id = #{userId} " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "AND (expires_at IS NULL OR expires_at > NOW()) " +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{size}")
    List<Notification> getUserNotifications(@Param("userId") Long userId, 
                                          @Param("status") String status,
                                          @Param("offset") Integer offset, 
                                          @Param("size") Integer size);

    /**
     * 获取未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND status = 'UNREAD' " +
            "AND (expires_at IS NULL OR expires_at > NOW())")
    Integer getUnreadCount(@Param("userId") Long userId);

    /**
     * 获取总通知数量
     */
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} " +
            "AND (expires_at IS NULL OR expires_at > NOW())")
    Integer getTotalCount(@Param("userId") Long userId);

    /**
     * 按类型获取通知数量
     */
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND type = #{type} " +
            "AND (expires_at IS NULL OR expires_at > NOW())")
    Integer getCountByType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 插入通知
     */
    @Insert("INSERT INTO notifications (user_id, title, content, type, status, priority, " +
            "action_url, action_text, created_at, expires_at) " +
            "VALUES (#{userId}, #{title}, #{content}, #{type}, #{status}, #{priority}, " +
            "#{actionUrl}, #{actionText}, #{createdAt}, #{expiresAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertNotification(Notification notification);

    /**
     * 标记通知为已读
     */
    @Update("UPDATE notifications SET status = 'READ', read_at = #{readAt} WHERE id = #{notificationId}")
    void markAsRead(@Param("notificationId") Long notificationId, @Param("readAt") LocalDateTime readAt);

    /**
     * 标记所有通知为已读
     */
    @Update("UPDATE notifications SET status = 'READ', read_at = #{readAt} " +
            "WHERE user_id = #{userId} AND status = 'UNREAD'")
    void markAllAsRead(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    /**
     * 删除通知
     */
    @Delete("DELETE FROM notifications WHERE id = #{id}")
    void deleteNotification(@Param("id") Long id);

    /**
     * 删除过期通知
     */
    @Delete("DELETE FROM notifications WHERE expires_at IS NOT NULL AND expires_at < #{now}")
    void deleteExpiredNotifications(@Param("now") LocalDateTime now);

    /**
     * 获取通知详情
     */
    @Select("SELECT * FROM notifications WHERE id = #{id}")
    Notification getNotificationById(@Param("id") Long id);
}
