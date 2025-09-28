package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.entity.Product;
import com.example.project2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test/products")
@CrossOrigin(origins = "*")
public class ProductTestController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public Result<String> testAddProduct() {
        try {
            Product product = new Product();
            product.setName("测试产品");
            product.setVariety("苹果");
            product.setSpecification("500g装");
            product.setPrice(new BigDecimal("15.50"));
            product.setStockQuantity(100);
            product.setBreedingCycle(30);
            product.setDescription("新鲜有机苹果");
            product.setSellerId(1L);
            product.setImageUrl("https://example.com/apple.jpg");
            product.setStatus(1);
            
            boolean success = productService.addProduct(product);
            if (success) {
                return Result.success("测试产品添加成功");
            } else {
                return Result.error("测试产品添加失败");
            }
        } catch (Exception e) {
            return Result.error("测试产品添加异常: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public Result<Map<String, Object>> checkProducts() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("totalProducts", productService.countAll());
            result.put("activeProducts", productService.countByStatus(1));
            result.put("message", "数据库连接正常");
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("检查产品失败: " + e.getMessage());
        }
    }
}
