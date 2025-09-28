package com.example.project2.mapper;

import com.example.project2.entity.ProductSupplier;
import com.example.project2.entity.SellerPurchaseOrder;
import com.example.project2.entity.SellerInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商城Mapper接口
 */
@Mapper
public interface MarketplaceMapper {

    /**
     * 获取产品供应商列表
     */
    List<ProductSupplier> getProductSuppliers(@Param("productId") Long productId);

    /**
     * 获取商城产品列表
     */
    List<Map<String, Object>> getMarketplaceProducts(@Param("params") Map<String, Object> params);

    /**
     * 创建采购订单
     */
    int createPurchaseOrder(SellerPurchaseOrder order);

    /**
     * 根据ID获取采购订单
     */
    SellerPurchaseOrder getPurchaseOrderById(@Param("id") Long id);

    /**
     * 获取采购方的采购订单
     */
    List<SellerPurchaseOrder> getBuyerPurchaseOrders(@Param("buyerId") Long buyerId);

    /**
     * 获取供应商的供应订单
     */
    List<SellerPurchaseOrder> getSupplierPurchaseOrders(@Param("supplierId") Long supplierId);

    /**
     * 更新采购订单状态
     */
    int updatePurchaseOrderStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新订单确认时间
     */
    int updatePurchaseOrderConfirmedTime(@Param("id") Long id, @Param("confirmedTime") LocalDateTime confirmedTime);

    /**
     * 更新订单发货时间
     */
    int updatePurchaseOrderShippedTime(@Param("id") Long id, @Param("shippedTime") LocalDateTime shippedTime);

    /**
     * 更新订单送达时间
     */
    int updatePurchaseOrderDeliveredTime(@Param("id") Long id, @Param("deliveredTime") LocalDateTime deliveredTime);

    /**
     * 获取销售方库存
     */
    List<SellerInventory> getSellerInventory(@Param("sellerId") Long sellerId);

    /**
     * 更新库存
     */
    int updateInventory(@Param("id") Long id, @Param("inventory") SellerInventory inventory);

    /**
     * 获取商城产品总数
     */
    int getTotalMarketplaceProducts();

    /**
     * 获取供应商总数
     */
    int getTotalSuppliers();

    /**
     * 获取采购订单总数
     */
    int getTotalPurchaseOrders();

    /**
     * 获取采购总金额
     */
    BigDecimal getTotalPurchaseAmount();

    /**
     * 获取活跃供应商数
     */
    int getActiveSuppliers();

    /**
     * 获取待处理订单数
     */
    int getPendingOrders();
}
