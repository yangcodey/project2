package com.example.project2.mapper;

import com.example.project2.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 根据ID查询订单
     */
    Order selectById(@Param("id") Long id);
    
    /**
     * 根据订单号查询订单
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 查询所有订单
     */
    List<Order> selectAll();
    
    /**
     * 根据购买方ID查询订单
     */
    List<Order> selectByBuyerId(@Param("buyerId") Long buyerId);
    
    /**
     * 根据产品ID查询订单
     */
    List<Order> selectByProductId(@Param("productId") Long productId);
    
    /**
     * 根据订单状态查询订单
     */
    List<Order> selectByStatus(@Param("status") String status);
    
    /**
     * 插入订单
     */
    int insert(Order order);
    
    /**
     * 更新订单
     */
    int update(Order order);
    
    /**
     * 根据ID删除订单
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 更新支付信息
     */
    int updatePayment(@Param("id") Long id, @Param("paymentMethod") String paymentMethod, @Param("paymentTime") String paymentTime);
    
    /**
     * 统计订单总数
     */
    int countAll();
    
    /**
     * 根据状态统计订单数量
     */
    int countByStatus(@Param("status") String status);
    
    /**
     * 获取订单总金额
     */
    Double getTotalAmount();
    
    /**
     * 获取平均订单金额
     */
    Double getAverageOrderValue();
    
    /**
     * 获取最大订单金额
     */
    Double getMaxOrderValue();
    
    /**
     * 获取最小订单金额
     */
    Double getMinOrderValue();
    
    /**
     * 获取订单趋势
     */
    List<Map<String, Object>> getOrderTrend();
}
