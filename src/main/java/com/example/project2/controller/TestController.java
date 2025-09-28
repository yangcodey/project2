package com.example.project2.controller;

import com.example.project2.common.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    /**
     * 测试CORS
     */
    @GetMapping("/cors")
    public Result<String> testCors() {
        return Result.success("CORS测试成功");
    }
    
    /**
     * 测试POST CORS
     */
    @PostMapping("/cors")
    public Result<String> testCorsPost(@RequestBody String data) {
        return Result.success("POST CORS测试成功: " + data);
    }
    
    /**
     * 测试产品API
     */
    @GetMapping("/products")
    public Result<String> testProducts() {
        return Result.success("Products API test successful!");
    }
}
