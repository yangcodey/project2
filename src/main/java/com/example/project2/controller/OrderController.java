package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.entity.Order;
import com.example.project2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 获取所有订单
     */
    @GetMapping
    public Result<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Result.success(orders);
    }
    
    /**
     * 根据ID获取订单
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return Result.success(order);
        } else {
            return Result.error("订单不存在");
        }
    }
    
    /**
     * 根据订单号获取订单
     */
    @GetMapping("/orderNo/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order != null) {
            return Result.success(order);
        } else {
            return Result.error("订单不存在");
        }
    }
    
    /**
     * 根据购买方ID获取订单
     */
    @GetMapping("/buyer/{buyerId}")
    public Result<List<Order>> getOrdersByBuyerId(@PathVariable Long buyerId) {
        List<Order> orders = orderService.getOrdersByBuyerId(buyerId);
        return Result.success(orders);
    }
    
    /**
     * 根据产品ID获取订单
     */
    @GetMapping("/product/{productId}")
    public Result<List<Order>> getOrdersByProductId(@PathVariable Long productId) {
        List<Order> orders = orderService.getOrdersByProductId(productId);
        return Result.success(orders);
    }
    
    /**
     * 根据订单状态获取订单
     */
    @GetMapping("/status/{status}")
    public Result<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return Result.success(orders);
    }
    
    /**
     * 创建订单
     */
    @PostMapping
    public Result<String> createOrder(@RequestBody Order order) {
        if (order.getProductId() == null || order.getBuyerId() == null || 
            order.getQuantity() == null || order.getUnitPrice() == null ||
            order.getDeliveryAddress() == null || order.getContactPhone() == null) {
            return Result.error("订单信息不完整");
        }
        
        try {
            boolean success = orderService.createOrder(order);
            if (success) {
                return Result.success("订单创建成功");
            } else {
                return Result.error("订单创建失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public Result<String> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        boolean success = orderService.updateOrder(order);
        if (success) {
            return Result.success("订单更新成功");
        } else {
            return Result.error("订单更新失败");
        }
    }
    
    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        String status = statusData.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        
        boolean success = orderService.updateOrderStatus(id, status);
        if (success) {
            return Result.success("订单状态更新成功");
        } else {
            return Result.error("订单状态更新失败");
        }
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    public Result<String> payOrder(@PathVariable Long id, @RequestBody Map<String, String> paymentData) {
        String paymentMethod = paymentData.get("paymentMethod");
        if (paymentMethod == null) {
            return Result.error("支付方式不能为空");
        }
        
        try {
            boolean success = orderService.payOrder(id, paymentMethod);
            if (success) {
                return Result.success("订单支付成功");
            } else {
                return Result.error("订单支付失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id) {
        try {
            boolean success = orderService.cancelOrder(id);
            if (success) {
                return Result.success("订单取消成功");
            } else {
                return Result.error("订单取消失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteOrder(@PathVariable Long id) {
        boolean success = orderService.deleteOrder(id);
        if (success) {
            return Result.success("订单删除成功");
        } else {
            return Result.error("订单删除失败");
        }
    }
}
