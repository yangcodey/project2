package com.example.project2.service;

import com.example.project2.entity.Product;
import com.example.project2.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品服务类
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 根据ID查询产品
     */
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }
    
    /**
     * 查询所有产品
     */
    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }
    
    /**
     * 根据销售方ID查询产品
     */
    public List<Product> getProductsBySellerId(Long sellerId) {
        return productMapper.selectBySellerId(sellerId);
    }
    
    /**
     * 根据产品名称模糊查询
     */
    public List<Product> searchProductsByName(String name) {
        return productMapper.selectByNameLike(name);
    }
    
    /**
     * 根据品种查询产品
     */
    public List<Product> getProductsByVariety(String variety) {
        return productMapper.selectByVariety(variety);
    }
    
    /**
     * 根据价格区间查询产品
     */
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productMapper.selectByPriceRange(minPrice, maxPrice);
    }
    
    /**
     * 添加产品
     */
    public boolean addProduct(Product product) {
        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        
        return productMapper.insert(product) > 0;
    }
    
    /**
     * 更新产品信息
     */
    public boolean updateProduct(Product product) {
        return productMapper.update(product) > 0;
    }
    
    /**
     * 更新库存数量
     */
    public boolean updateStock(Long id, Integer quantity) {
        return productMapper.updateStock(id, quantity) > 0;
    }
    
    /**
     * 更新产品状态
     */
    public boolean updateProductStatus(Long id, String status) {
        // 将状态字符串转换为数字
        Integer statusCode = getStatusCode(status);
        return productMapper.updateStatus(id, statusCode) > 0;
    }
    
    /**
     * 更新产品状态（兼容旧方法）
     */
    public boolean updateProductStatus(Long id, Integer status) {
        return productMapper.updateStatus(id, status) > 0;
    }
    
    /**
     * 获取产品列表（支持分页和筛选）
     */
    public Map<String, Object> getProducts(int page, int size, String keyword, String status, String variety) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 构建查询条件
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("status", status != null ? getStatusCode(status) : null);
        params.put("variety", variety);
        params.put("offset", offset);
        params.put("size", size);
        
        // 查询产品列表
        List<Product> products = productMapper.selectWithPagination(params);
        
        // 查询总数
        int total = productMapper.countWithFilters(params);
        
        result.put("records", products);
        result.put("total", total);
        result.put("current", page);
        result.put("size", size);
        
        return result;
    }
    
    /**
     * 将状态字符串转换为数字
     */
    private Integer getStatusCode(String status) {
        if (status == null) return null;
        
        switch (status.toUpperCase()) {
            case "ACTIVE":
                return 1; // 上架中
            case "INACTIVE":
                return 0; // 已下架
            case "OUT_OF_STOCK":
                return 2; // 售罄
            default:
                return 1; // 默认上架
        }
    }
    
    /**
     * 减少库存（购买时调用）
     */
    public boolean decreaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }
        
        int newStock = product.getStockQuantity() - quantity;
        if (newStock < 0) {
            return false; // 库存不足
        }
        
        // 更新库存
        boolean success = updateStock(productId, newStock);
        
        // 如果库存为0，自动设置为售罄状态
        if (success && newStock == 0) {
            updateProductStatus(productId, "OUT_OF_STOCK");
        }
        
        return success;
    }
    
    /**
     * 增加库存（取消订单时调用）
     */
    public boolean increaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }
        
        int newStock = product.getStockQuantity() + quantity;
        return updateStock(productId, newStock);
    }
    
    /**
     * 删除产品
     */
    public boolean deleteProduct(Long id) {
        return productMapper.deleteById(id) > 0;
    }
    
    /**
     * 统计产品总数
     */
    public int countAll() {
        return productMapper.countAll();
    }
    
    /**
     * 根据状态统计产品数量
     */
    public int countByStatus(Integer status) {
        return productMapper.countByStatus(status);
    }
    
    /**
     * 获取产品销量统计
     */
    public List<Map<String, Object>> getProductSalesStats() {
        return productMapper.getProductSalesStats();
    }
    
    /**
     * 获取产品品种分布
     */
    public List<Map<String, Object>> getVarietyDistribution() {
        return productMapper.getVarietyDistribution();
    }
    
    /**
     * 获取价格区间分布
     */
    public List<Map<String, Object>> getPriceDistribution() {
        return productMapper.getPriceDistribution();
    }
    
    /**
     * 获取总库存
     */
    public int getTotalStock() {
        return productMapper.getTotalStock();
    }
    
    /**
     * 获取低库存产品数量
     */
    public int getLowStockCount(Integer threshold) {
        return productMapper.getLowStockCount(threshold);
    }
}
