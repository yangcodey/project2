package com.example.project2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class IndexController {
    
    /**
     * 访问根路径时重定向到首页
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
    
    /**
     * 访问用户页面
     */
    @GetMapping("/user")
    public String user() {
        return "forward:/user.html";
    }
    
    /**
     * 访问用户界面
     */
    @GetMapping("/ui")
    public String ui() {
        return "forward:/user.html";
    }
    
    /**
     * 访问产品展示页面
     */
    @GetMapping("/products")
    public String products() {
        return "forward:/products.html";
    }
}
