package com.example.project2.util;

import org.springframework.stereotype.Component;

/**
 * JWT工具类（简化版本）
 */
@Component
public class JwtUtil {
    
    /**
     * 生成JWT token（简化版本）
     */
    public String generateToken(String username, Long userId, String role) {
        // 简化版本，直接返回用户名+时间戳作为token
        return username + "_" + userId + "_" + role + "_" + System.currentTimeMillis();
    }
    
    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        if (token != null && token.contains("_")) {
            return token.split("_")[0];
        }
        return null;
    }
    
    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        if (token != null && token.contains("_")) {
            String[] parts = token.split("_");
            if (parts.length > 1) {
                try {
                    return Long.valueOf(parts[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * 从token中获取用户角色
     */
    public String getRoleFromToken(String token) {
        if (token != null && token.contains("_")) {
            String[] parts = token.split("_");
            if (parts.length > 2) {
                return parts[2];
            }
        }
        return null;
    }
    
    /**
     * 判断token是否过期（简化版本，24小时过期）
     */
    public Boolean isTokenExpired(String token) {
        if (token != null && token.contains("_")) {
            String[] parts = token.split("_");
            if (parts.length > 3) {
                try {
                    long tokenTime = Long.parseLong(parts[3]);
                    long currentTime = System.currentTimeMillis();
                    // 24小时过期
                    return (currentTime - tokenTime) > 24 * 60 * 60 * 1000;
                } catch (NumberFormatException e) {
                    return true;
                }
            }
        }
        return true;
    }
    
    /**
     * 验证token
     */
    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (username != null && username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
