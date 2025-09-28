package com.example.project2.mapper;

import com.example.project2.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 产品Mapper接口
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 根据ID查询产品
     */
    Product selectById(@Param("id") Long id);
    
    /**
     * 查询所有产品
     */
    List<Product> selectAll();
    
    /**
     * 根据销售方ID查询产品
     */
    List<Product> selectBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * 根据产品名称模糊查询
     */
    List<Product> selectByNameLike(@Param("name") String name);
    
    /**
     * 根据品种查询产品
     */
    List<Product> selectByVariety(@Param("variety") String variety);
    
    /**
     * 根据价格区间查询产品
     */
    List<Product> selectByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    /**
     * 插入产品
     */
    int insert(Product product);
    
    /**
     * 更新产品
     */
    int update(Product product);
    
    /**
     * 根据ID删除产品
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新库存数量
     */
    int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 更新产品状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 统计产品总数
     */
    int countAll();
    
    /**
     * 根据状态统计产品数量
     */
    int countByStatus(@Param("status") Integer status);
    
    /**
     * 获取产品销量统计
     */
    List<Map<String, Object>> getProductSalesStats();
    
    /**
     * 获取产品品种分布
     */
    List<Map<String, Object>> getVarietyDistribution();
    
    /**
     * 获取价格区间分布
     */
    List<Map<String, Object>> getPriceDistribution();
    
    /**
     * 获取总库存
     */
    int getTotalStock();
    
    /**
     * 获取低库存产品数量
     */
    int getLowStockCount(@Param("threshold") Integer threshold);
    
    /**
     * 分页查询产品（支持筛选）
     */
    List<Product> selectWithPagination(@Param("params") Map<String, Object> params);
    
    /**
     * 统计筛选条件下的产品总数
     */
    int countWithFilters(@Param("params") Map<String, Object> params);
}
