package com.example.project2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Favicon控制器 - 处理浏览器自动请求的favicon.ico
 */
@RestController
public class FaviconController {
    
    /**
     * 处理favicon.ico请求
     * 返回204 No Content，避免404错误
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}





