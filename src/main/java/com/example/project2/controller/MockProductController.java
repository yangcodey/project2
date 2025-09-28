package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.entity.Product;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 模拟产品控制器（用于测试）
 */
@RestController
@RequestMapping("/api/mock")
@CrossOrigin(origins = "*")
public class MockProductController {

    @GetMapping("/products")
    public Result<List<Product>> getMockProducts() {
        List<Product> products = new ArrayList<>();
        
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("优质土鸡");
        product1.setVariety("土鸡");
        product1.setSpecification("1-1.5kg");
        product1.setPrice(new BigDecimal("25.00"));
        product1.setStockQuantity(100);
        product1.setBreedingCycle(120);
        product1.setDescription("散养土鸡，肉质鲜美");
        product1.setSellerId(1L);
        product1.setImageUrl("/images/chicken.jpg");
        product1.setStatus(1);
        product1.setCreatedAt(LocalDateTime.now());
        product1.setUpdatedAt(LocalDateTime.now());
        products.add(product1);
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("生态鸭");
        product2.setVariety("麻鸭");
        product2.setSpecification("1.5-2kg");
        product2.setPrice(new BigDecimal("30.00"));
        product2.setStockQuantity(80);
        product2.setBreedingCycle(90);
        product2.setDescription("生态养殖，营养丰富");
        product2.setSellerId(1L);
        product2.setImageUrl("/images/duck.jpg");
        product2.setStatus(1);
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        products.add(product2);
        
        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("有机鹅");
        product3.setVariety("白鹅");
        product3.setSpecification("2-3kg");
        product3.setPrice(new BigDecimal("45.00"));
        product3.setStockQuantity(50);
        product3.setBreedingCycle(150);
        product3.setDescription("有机认证，绿色健康");
        product3.setSellerId(1L);
        product3.setImageUrl("/images/goose.jpg");
        product3.setStatus(1);
        product3.setCreatedAt(LocalDateTime.now());
        product3.setUpdatedAt(LocalDateTime.now());
        products.add(product3);
        
        return Result.success(products);
    }
}
