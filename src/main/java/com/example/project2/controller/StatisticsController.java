package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据分析控制器
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private BreedingPlanMapper breedingPlanMapper;
    
    @Autowired
    private LogisticsMapper logisticsMapper;
    
    /**
     * 获取系统概览统计
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverviewStatistics() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // 用户统计
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("total", userMapper.countAll());
            userStats.put("admin", userMapper.countByRole("ADMIN"));
            userStats.put("seller", userMapper.countByRole("SELLER"));
            userStats.put("buyer", userMapper.countByRole("BUYER"));
            userStats.put("active", userMapper.countByStatus(1));
            overview.put("users", userStats);
            
            // 产品统计
            Map<String, Object> productStats = new HashMap<>();
            productStats.put("total", productMapper.countAll());
            productStats.put("active", productMapper.countByStatus(1));
            productStats.put("inactive", productMapper.countByStatus(0));
            overview.put("products", productStats);
            
            // 订单统计
            Map<String, Object> orderStats = new HashMap<>();
            orderStats.put("total", orderMapper.countAll());
            orderStats.put("pending", orderMapper.countByStatus("PENDING_PAYMENT"));
            orderStats.put("paid", orderMapper.countByStatus("PAID"));
            orderStats.put("shipped", orderMapper.countByStatus("SHIPPED"));
            orderStats.put("delivered", orderMapper.countByStatus("DELIVERED"));
            orderStats.put("completed", orderMapper.countByStatus("COMPLETED"));
            overview.put("orders", orderStats);
            
            // 养殖计划统计
            Map<String, Object> planStats = new HashMap<>();
            planStats.put("total", breedingPlanMapper.countAll());
            planStats.put("planning", breedingPlanMapper.countByStatus("PLANNING"));
            planStats.put("inProgress", breedingPlanMapper.countByStatus("IN_PROGRESS"));
            planStats.put("completed", breedingPlanMapper.countByStatus("COMPLETED"));
            planStats.put("cancelled", breedingPlanMapper.countByStatus("CANCELLED"));
            overview.put("breedingPlans", planStats);
            
            // 物流统计
            Map<String, Object> logisticsStats = new HashMap<>();
            logisticsStats.put("total", logisticsMapper.countAll());
            logisticsStats.put("shipped", logisticsMapper.countByStatus("SHIPPED"));
            logisticsStats.put("inTransit", logisticsMapper.countByStatus("IN_TRANSIT"));
            logisticsStats.put("arrived", logisticsMapper.countByStatus("ARRIVED"));
            logisticsStats.put("delivered", logisticsMapper.countByStatus("DELIVERED"));
            overview.put("logistics", logisticsStats);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取概览统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取销售数据统计
     */
    @GetMapping("/sales")
    public Result<Map<String, Object>> getSalesStatistics(@RequestParam(required = false) String startDate, 
                                                          @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> salesStats = new HashMap<>();
            
            // 如果没有指定日期范围，使用最近30天
            if (startDate == null || endDate == null) {
                LocalDate end = LocalDate.now();
                LocalDate start = end.minusDays(30);
                startDate = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                endDate = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            // 订单统计
            Map<String, Object> orderStats = new HashMap<>();
            orderStats.put("totalOrders", orderMapper.countAll());
            orderStats.put("totalAmount", orderMapper.getTotalAmount());
            orderStats.put("averageOrderValue", orderMapper.getAverageOrderValue());
            salesStats.put("orders", orderStats);
            
            // 产品销量统计
            List<Map<String, Object>> productSales = productMapper.getProductSalesStats();
            salesStats.put("productSales", productSales);
            
            // 用户购买统计
            List<Map<String, Object>> userPurchases = userMapper.getUserPurchaseStats();
            salesStats.put("userPurchases", userPurchases);
            
            return Result.success(salesStats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取销售统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户分析数据
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserAnalysis() {
        try {
            Map<String, Object> userAnalysis = new HashMap<>();
            
            // 用户角色分布
            Map<String, Object> roleDistribution = new HashMap<>();
            roleDistribution.put("admin", userMapper.countByRole("ADMIN"));
            roleDistribution.put("seller", userMapper.countByRole("SELLER"));
            roleDistribution.put("buyer", userMapper.countByRole("BUYER"));
            userAnalysis.put("roleDistribution", roleDistribution);
            
            // 用户状态分布
            Map<String, Object> statusDistribution = new HashMap<>();
            statusDistribution.put("active", userMapper.countByStatus(1));
            statusDistribution.put("inactive", userMapper.countByStatus(0));
            userAnalysis.put("statusDistribution", statusDistribution);
            
            // 用户注册趋势（最近30天）
            List<Map<String, Object>> registrationTrend = userMapper.getRegistrationTrend();
            userAnalysis.put("registrationTrend", registrationTrend);
            
            return Result.success(userAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取产品分析数据
     */
    @GetMapping("/products")
    public Result<Map<String, Object>> getProductAnalysis() {
        try {
            Map<String, Object> productAnalysis = new HashMap<>();
            
            // 产品状态分布
            Map<String, Object> statusDistribution = new HashMap<>();
            statusDistribution.put("active", productMapper.countByStatus(1));
            statusDistribution.put("inactive", productMapper.countByStatus(0));
            productAnalysis.put("statusDistribution", statusDistribution);
            
            // 产品品种分布
            List<Map<String, Object>> varietyDistribution = productMapper.getVarietyDistribution();
            productAnalysis.put("varietyDistribution", varietyDistribution);
            
            // 价格区间分布
            List<Map<String, Object>> priceDistribution = productMapper.getPriceDistribution();
            productAnalysis.put("priceDistribution", priceDistribution);
            
            // 库存统计
            Map<String, Object> stockStats = new HashMap<>();
            stockStats.put("totalStock", productMapper.getTotalStock());
            stockStats.put("lowStockCount", productMapper.getLowStockCount(10));
            productAnalysis.put("stockStats", stockStats);
            
            return Result.success(productAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单分析数据
     */
    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrderAnalysis() {
        try {
            Map<String, Object> orderAnalysis = new HashMap<>();
            
            // 订单状态分布
            Map<String, Object> statusDistribution = new HashMap<>();
            statusDistribution.put("pending", orderMapper.countByStatus("PENDING_PAYMENT"));
            statusDistribution.put("paid", orderMapper.countByStatus("PAID"));
            statusDistribution.put("shipped", orderMapper.countByStatus("SHIPPED"));
            statusDistribution.put("delivered", orderMapper.countByStatus("DELIVERED"));
            statusDistribution.put("completed", orderMapper.countByStatus("COMPLETED"));
            statusDistribution.put("cancelled", orderMapper.countByStatus("CANCELLED"));
            orderAnalysis.put("statusDistribution", statusDistribution);
            
            // 订单金额统计
            Map<String, Object> amountStats = new HashMap<>();
            amountStats.put("totalAmount", orderMapper.getTotalAmount());
            amountStats.put("averageAmount", orderMapper.getAverageOrderValue());
            amountStats.put("maxAmount", orderMapper.getMaxOrderValue());
            amountStats.put("minAmount", orderMapper.getMinOrderValue());
            orderAnalysis.put("amountStats", amountStats);
            
            // 订单趋势（最近30天）
            List<Map<String, Object>> orderTrend = orderMapper.getOrderTrend();
            orderAnalysis.put("orderTrend", orderTrend);
            
            return Result.success(orderAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取养殖计划分析数据
     */
    @GetMapping("/breeding-plans")
    public Result<Map<String, Object>> getBreedingPlanAnalysis() {
        try {
            Map<String, Object> planAnalysis = new HashMap<>();
            
            // 计划状态分布
            Map<String, Object> statusDistribution = new HashMap<>();
            statusDistribution.put("planning", breedingPlanMapper.countByStatus("PLANNING"));
            statusDistribution.put("inProgress", breedingPlanMapper.countByStatus("IN_PROGRESS"));
            statusDistribution.put("completed", breedingPlanMapper.countByStatus("COMPLETED"));
            statusDistribution.put("cancelled", breedingPlanMapper.countByStatus("CANCELLED"));
            planAnalysis.put("statusDistribution", statusDistribution);
            
            // 计划数量统计
            Map<String, Object> quantityStats = new HashMap<>();
            quantityStats.put("totalPlanned", breedingPlanMapper.getTotalPlannedQuantity());
            quantityStats.put("averagePlanned", breedingPlanMapper.getAveragePlannedQuantity());
            planAnalysis.put("quantityStats", quantityStats);
            
            return Result.success(planAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取养殖计划分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取物流分析数据
     */
    @GetMapping("/logistics")
    public Result<Map<String, Object>> getLogisticsAnalysis() {
        try {
            Map<String, Object> logisticsAnalysis = new HashMap<>();
            
            // 物流状态分布
            Map<String, Object> statusDistribution = new HashMap<>();
            statusDistribution.put("shipped", logisticsMapper.countByStatus("SHIPPED"));
            statusDistribution.put("inTransit", logisticsMapper.countByStatus("IN_TRANSIT"));
            statusDistribution.put("arrived", logisticsMapper.countByStatus("ARRIVED"));
            statusDistribution.put("delivered", logisticsMapper.countByStatus("DELIVERED"));
            logisticsAnalysis.put("statusDistribution", statusDistribution);
            
            // 物流公司分布
            List<Map<String, Object>> companyDistribution = logisticsMapper.getCompanyDistribution();
            logisticsAnalysis.put("companyDistribution", companyDistribution);
            
            return Result.success(logisticsAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取物流分析失败: " + e.getMessage());
        }
    }
}
