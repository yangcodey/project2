package com.example.project2.service;

import com.example.project2.entity.Order;
import com.example.project2.entity.Product;
import com.example.project2.mapper.OrderMapper;
import com.example.project2.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务类
 */
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 根据ID查询订单
     */
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }
    
    /**
     * 根据订单号查询订单
     */
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }
    
    /**
     * 查询所有订单
     */
    public List<Order> getAllOrders() {
        return orderMapper.selectAll();
    }
    
    /**
     * 根据购买方ID查询订单
     */
    public List<Order> getOrdersByBuyerId(Long buyerId) {
        return orderMapper.selectByBuyerId(buyerId);
    }
    
    /**
     * 根据产品ID查询订单
     */
    public List<Order> getOrdersByProductId(Long productId) {
        return orderMapper.selectByProductId(productId);
    }
    
    /**
     * 根据订单状态查询订单
     */
    public List<Order> getOrdersByStatus(String status) {
        return orderMapper.selectByStatus(status);
    }
    
    /**
     * 创建订单
     */
    @Transactional
    public boolean createOrder(Order order) {
        // 检查产品是否存在且库存充足
        Product product = productMapper.selectById(order.getProductId());
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }
        
        if (product.getStockQuantity() < order.getQuantity()) {
            throw new RuntimeException("库存不足");
        }
        
        // 生成订单号
        String orderNo = "NBWY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        order.setOrderNo(orderNo);
        
        // 设置订单时间
        order.setOrderTime(LocalDateTime.now());
        
        // 设置订单状态
        order.setStatus("PENDING_PAYMENT");
        
        // 插入订单
        boolean success = orderMapper.insert(order) > 0;
        
        if (success) {
            // 减少库存
            int newStock = product.getStockQuantity() - order.getQuantity();
            productMapper.updateStock(order.getProductId(), newStock);
        }
        
        return success;
    }
    
    /**
     * 更新订单
     */
    public boolean updateOrder(Order order) {
        return orderMapper.update(order) > 0;
    }
    
    /**
     * 更新订单状态
     */
    public boolean updateOrderStatus(Long id, String status) {
        return orderMapper.updateStatus(id, status) > 0;
    }
    
    /**
     * 支付订单
     */
    @Transactional
    public boolean payOrder(Long id, String paymentMethod) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }
        
        // 更新支付信息
        boolean success = orderMapper.updatePayment(id, paymentMethod, LocalDateTime.now().toString()) > 0;
        
        if (success) {
            // 更新订单状态为已支付
            orderMapper.updateStatus(id, "PAID");
        }
        
        return success;
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public boolean cancelOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if ("CANCELLED".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法取消");
        }
        
        // 更新订单状态为已取消
        boolean success = orderMapper.updateStatus(id, "CANCELLED") > 0;
        
        if (success && "PAID".equals(order.getStatus())) {
            // 如果订单已支付，需要恢复库存
            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                int newStock = product.getStockQuantity() + order.getQuantity();
                productMapper.updateStock(order.getProductId(), newStock);
            }
        }
        
        return success;
    }
    
    /**
     * 删除订单
     */
    public boolean deleteOrder(Long id) {
        return orderMapper.deleteById(id) > 0;
    }
}
