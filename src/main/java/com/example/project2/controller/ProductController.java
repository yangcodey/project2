package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.entity.Product;
import com.example.project2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 产品控制器
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 获取所有产品
     */
    @GetMapping
    public Result<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取产品
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return Result.success(product);
        } else {
            return Result.error("产品不存在");
        }
    }
    
    /**
     * 根据销售方ID获取产品
     */
    @GetMapping("/seller/{sellerId}")
    public Result<List<Product>> getProductsBySellerId(@PathVariable Long sellerId) {
        List<Product> products = productService.getProductsBySellerId(sellerId);
        return Result.success(products);
    }

    /**
     * 获取当前销售方的产品（需要登录）
     */
    @GetMapping("/my-products")
    public Result<List<Product>> getMyProducts(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中解析用户ID（这里简化处理，实际应该解析JWT）
            // 暂时返回所有产品，实际应该根据token解析用户ID
            List<Product> products = productService.getAllProducts();
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据产品名称搜索
     */
    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return Result.success(products);
    }
    
    /**
     * 根据品种获取产品
     */
    @GetMapping("/variety/{variety}")
    public Result<List<Product>> getProductsByVariety(@PathVariable String variety) {
        List<Product> products = productService.getProductsByVariety(variety);
        return Result.success(products);
    }
    
    /**
     * 根据价格区间获取产品
     */
    @GetMapping("/price")
    public Result<List<Product>> getProductsByPriceRange(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return Result.success(products);
    }
    
    /**
     * 添加产品
     */
    @PostMapping
    public Result<String> addProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getVariety() == null || product.getPrice() == null) {
            return Result.error("产品名称、品种和价格不能为空");
        }
        
        boolean success = productService.addProduct(product);
        if (success) {
            return Result.success("产品添加成功");
        } else {
            return Result.error("产品添加失败");
        }
    }
    
    /**
     * 更新产品信息
     */
    @PutMapping("/{id}")
    public Result<String> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        boolean success = productService.updateProduct(product);
        if (success) {
            return Result.success("产品更新成功");
        } else {
            return Result.error("产品更新失败");
        }
    }
    
    /**
     * 更新库存
     */
    @PutMapping("/{id}/stock")
    public Result<String> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> stockData) {
        Integer quantity = stockData.get("quantity");
        if (quantity == null) {
            return Result.error("库存数量不能为空");
        }
        
        boolean success = productService.updateStock(id, quantity);
        if (success) {
            return Result.success("库存更新成功");
        } else {
            return Result.error("库存更新失败");
        }
    }
    
    /**
     * 更新产品状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        String status = statusData.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        
        boolean success = productService.updateProductStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
    
    /**
     * 获取产品列表（支持分页和筛选）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String variety) {
        try {
            Map<String, Object> result = productService.getProducts(page, size, keyword, status, variety);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除产品
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        boolean success = productService.deleteProduct(id);
        if (success) {
            return Result.success("产品删除成功");
        } else {
            return Result.error("产品删除失败");
        }
    }
}
