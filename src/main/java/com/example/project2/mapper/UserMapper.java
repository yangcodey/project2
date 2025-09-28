package com.example.project2.mapper;

import com.example.project2.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);
    
    /**
     * 查询所有用户
     */
    List<User> selectAll();
    
    /**
     * 根据角色查询用户
     */
    List<User> selectByRole(@Param("role") String role);
    
    /**
     * 插入用户
     */
    int insert(User user);
    
    /**
     * 更新用户
     */
    int update(User user);
    
    /**
     * 根据ID删除用户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据用户名和密码查询用户（登录验证）
     */
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    /**
     * 更新用户状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 统计用户总数
     */
    int countAll();
    
    /**
     * 根据角色统计用户数量
     */
    int countByRole(@Param("role") String role);
    
    /**
     * 根据状态统计用户数量
     */
    int countByStatus(@Param("status") Integer status);
    
    /**
     * 获取用户注册趋势
     */
    List<Map<String, Object>> getRegistrationTrend();
    
    /**
     * 获取用户购买统计
     */
    List<Map<String, Object>> getUserPurchaseStats();
}
