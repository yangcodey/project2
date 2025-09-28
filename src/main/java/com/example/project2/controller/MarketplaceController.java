package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.entity.ProductSupplier;
import com.example.project2.entity.SellerPurchaseOrder;
import com.example.project2.entity.SellerInventory;
import com.example.project2.service.MarketplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商城控制器
 */
@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    @Autowired
    private MarketplaceService marketplaceService;

    /**
     * 获取产品供应商列表
     */
    @GetMapping("/suppliers/{productId}")
    public Result<List<ProductSupplier>> getProductSuppliers(@PathVariable Long productId) {
        try {
            List<ProductSupplier> suppliers = marketplaceService.getProductSuppliers(productId);
            return Result.success(suppliers);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取供应商列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有可采购的产品
     */
    @GetMapping("/products")
    public Result<List<Map<String, Object>>> getMarketplaceProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String variety) {
        try {
            List<Map<String, Object>> products = marketplaceService.getMarketplaceProducts(keyword, categoryId, variety);
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取商城产品失败: " + e.getMessage());
        }
    }

    /**
     * 创建采购订单
     */
    @PostMapping("/purchase-orders")
    public Result<String> createPurchaseOrder(@RequestBody SellerPurchaseOrder order) {
        try {
            boolean success = marketplaceService.createPurchaseOrder(order);
            if (success) {
                return Result.success("采购订单创建成功");
            } else {
                return Result.error("采购订单创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建采购订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售方的采购订单
     */
    @GetMapping("/purchase-orders/buyer/{buyerId}")
    public Result<List<SellerPurchaseOrder>> getBuyerPurchaseOrders(@PathVariable Long buyerId) {
        try {
            List<SellerPurchaseOrder> orders = marketplaceService.getBuyerPurchaseOrders(buyerId);
            return Result.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取采购订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售方的供应订单
     */
    @GetMapping("/purchase-orders/supplier/{supplierId}")
    public Result<List<SellerPurchaseOrder>> getSupplierPurchaseOrders(@PathVariable Long supplierId) {
        try {
            List<SellerPurchaseOrder> orders = marketplaceService.getSupplierPurchaseOrders(supplierId);
            return Result.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取供应订单失败: " + e.getMessage());
        }
    }

    /**
     * 更新采购订单状态
     */
    @PutMapping("/purchase-orders/{id}/status")
    public Result<String> updatePurchaseOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            boolean success = marketplaceService.updatePurchaseOrderStatus(id, status);
            if (success) {
                return Result.success("订单状态更新成功");
            } else {
                return Result.error("订单状态更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新订单状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售方库存
     */
    @GetMapping("/inventory/{sellerId}")
    public Result<List<SellerInventory>> getSellerInventory(@PathVariable Long sellerId) {
        try {
            List<SellerInventory> inventory = marketplaceService.getSellerInventory(sellerId);
            return Result.success(inventory);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取库存信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新库存
     */
    @PutMapping("/inventory/{id}")
    public Result<String> updateInventory(@PathVariable Long id, @RequestBody SellerInventory inventory) {
        try {
            boolean success = marketplaceService.updateInventory(id, inventory);
            if (success) {
                return Result.success("库存更新成功");
            } else {
                return Result.error("库存更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新库存失败: " + e.getMessage());
        }
    }

    /**
     * 获取商城统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getMarketplaceStats() {
        try {
            Map<String, Object> stats = marketplaceService.getMarketplaceStats();
            return Result.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
}
