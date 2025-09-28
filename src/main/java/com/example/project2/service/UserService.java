package com.example.project2.service;

import com.example.project2.entity.User;
import com.example.project2.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务类
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }
    
    /**
     * 根据角色查询用户
     */
    public List<User> getUsersByRole(String role) {
        return userMapper.selectByRole(role);
    }
    
    /**
     * 用户登录验证
     */
    public User login(String username, String password) {
        return userMapper.selectByUsernameAndPassword(username, password);
    }
    
    /**
     * 用户注册
     */
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }
        
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getRole() == null) {
            user.setRole("BUYER");
        }
        
        return userMapper.insert(user) > 0;
    }
    
    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        return userMapper.update(user) > 0;
    }
    
    /**
     * 更新用户状态
     */
    public boolean updateUserStatus(Long id, Integer status) {
        return userMapper.updateStatus(id, status) > 0;
    }
    
    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}
