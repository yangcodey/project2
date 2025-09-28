package com.example.project2.service;

import com.example.project2.entity.ProductSupplier;
import com.example.project2.entity.SellerPurchaseOrder;
import com.example.project2.entity.SellerInventory;
import com.example.project2.mapper.MarketplaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 商城服务类
 */
@Service
public class MarketplaceService {

    @Autowired
    private MarketplaceMapper marketplaceMapper;

    /**
     * 获取产品供应商列表
     */
    public List<ProductSupplier> getProductSuppliers(Long productId) {
        return marketplaceMapper.getProductSuppliers(productId);
    }

    /**
     * 获取商城产品列表
     */
    public List<Map<String, Object>> getMarketplaceProducts(String keyword, Long categoryId, String variety) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("categoryId", categoryId);
        params.put("variety", variety);
        return marketplaceMapper.getMarketplaceProducts(params);
    }

    /**
     * 创建采购订单
     */
    @Transactional
    public boolean createPurchaseOrder(SellerPurchaseOrder order) {
        // 生成订单号
        String orderNo = "SPO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        order.setOrderNo(orderNo);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("PENDING");

        // 计算总金额
        order.setTotalAmount(order.getUnitPrice().multiply(new java.math.BigDecimal(order.getQuantity())));

        return marketplaceMapper.createPurchaseOrder(order) > 0;
    }

    /**
     * 获取采购方的采购订单
     */
    public List<SellerPurchaseOrder> getBuyerPurchaseOrders(Long buyerId) {
        return marketplaceMapper.getBuyerPurchaseOrders(buyerId);
    }

    /**
     * 获取供应商的供应订单
     */
    public List<SellerPurchaseOrder> getSupplierPurchaseOrders(Long supplierId) {
        return marketplaceMapper.getSupplierPurchaseOrders(supplierId);
    }

    /**
     * 更新采购订单状态
     */
    @Transactional
    public boolean updatePurchaseOrderStatus(Long id, String status) {
        SellerPurchaseOrder order = marketplaceMapper.getPurchaseOrderById(id);
        if (order == null) {
            return false;
        }

        // 更新状态
        boolean success = marketplaceMapper.updatePurchaseOrderStatus(id, status) > 0;

        if (success) {
            // 根据状态更新时间
            LocalDateTime now = LocalDateTime.now();
            switch (status) {
                case "CONFIRMED":
                    marketplaceMapper.updatePurchaseOrderConfirmedTime(id, now);
                    break;
                case "SHIPPED":
                    marketplaceMapper.updatePurchaseOrderShippedTime(id, now);
                    break;
                case "DELIVERED":
                    marketplaceMapper.updatePurchaseOrderDeliveredTime(id, now);
                    break;
            }
        }

        return success;
    }

    /**
     * 获取销售方库存
     */
    public List<SellerInventory> getSellerInventory(Long sellerId) {
        return marketplaceMapper.getSellerInventory(sellerId);
    }

    /**
     * 更新库存
     */
    public boolean updateInventory(Long id, SellerInventory inventory) {
        return marketplaceMapper.updateInventory(id, inventory) > 0;
    }

    /**
     * 获取商城统计数据
     */
    public Map<String, Object> getMarketplaceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取各种统计数据
        stats.put("totalProducts", marketplaceMapper.getTotalMarketplaceProducts());
        stats.put("totalSuppliers", marketplaceMapper.getTotalSuppliers());
        stats.put("totalPurchaseOrders", marketplaceMapper.getTotalPurchaseOrders());
        stats.put("totalPurchaseAmount", marketplaceMapper.getTotalPurchaseAmount());
        stats.put("activeSuppliers", marketplaceMapper.getActiveSuppliers());
        stats.put("pendingOrders", marketplaceMapper.getPendingOrders());
        
        return stats;
    }
}
