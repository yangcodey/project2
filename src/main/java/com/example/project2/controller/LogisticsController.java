package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.entity.Logistics;
import com.example.project2.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流控制器
 */
@RestController
@RequestMapping("/api/logistics")
@CrossOrigin(origins = "*")
public class LogisticsController {
    
    @Autowired
    private LogisticsService logisticsService;
    
    /**
     * 获取所有物流信息
     */
    @GetMapping
    public Result<List<Logistics>> getAllLogistics() {
        try {
            List<Logistics> logistics = logisticsService.getAllLogistics();
            return Result.success(logistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取物流信息列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取物流信息
     */
    @GetMapping("/{id}")
    public Result<Logistics> getLogisticsById(@PathVariable Long id) {
        try {
            Logistics logistics = logisticsService.getLogisticsById(id);
            if (logistics != null) {
                return Result.success(logistics);
            } else {
                return Result.error("物流信息不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据订单ID获取物流信息
     */
    @GetMapping("/order/{orderId}")
    public Result<List<Logistics>> getLogisticsByOrderId(@PathVariable Long orderId) {
        try {
            List<Logistics> logistics = logisticsService.getLogisticsByOrderId(orderId);
            return Result.success(logistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据物流单号查询物流信息
     */
    @GetMapping("/tracking/{trackingNo}")
    public Result<Logistics> getLogisticsByTrackingNo(@PathVariable String trackingNo) {
        try {
            Logistics logistics = logisticsService.getLogisticsByTrackingNo(trackingNo);
            if (logistics != null) {
                return Result.success(logistics);
            } else {
                return Result.error("物流信息不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据状态获取物流信息
     */
    @GetMapping("/status/{status}")
    public Result<List<Logistics>> getLogisticsByStatus(@PathVariable String status) {
        try {
            List<Logistics> logistics = logisticsService.getLogisticsByStatus(status);
            return Result.success(logistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取状态物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据物流公司获取物流信息
     */
    @GetMapping("/company/{company}")
    public Result<List<Logistics>> getLogisticsByCompany(@PathVariable String company) {
        try {
            List<Logistics> logistics = logisticsService.getLogisticsByCompany(company);
            return Result.success(logistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取公司物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加物流信息
     */
    @PostMapping
    public Result<String> addLogistics(@RequestBody Logistics logistics) {
        try {
            if (logistics.getOrderId() == null || logistics.getCompany() == null || 
                logistics.getTrackingNo() == null) {
                return Result.error("订单ID、物流公司和物流单号不能为空");
            }
            
            boolean success = logisticsService.addLogistics(logistics);
            if (success) {
                return Result.success("物流信息添加成功");
            } else {
                return Result.error("物流信息添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新物流信息
     */
    @PutMapping("/{id}")
    public Result<String> updateLogistics(@PathVariable Long id, @RequestBody Logistics logistics) {
        try {
            logistics.setId(id);
            boolean success = logisticsService.updateLogistics(logistics);
            if (success) {
                return Result.success("物流信息更新成功");
            } else {
                return Result.error("物流信息更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新物流状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateLogisticsStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            if (status == null || status.isEmpty()) {
                return Result.error("状态不能为空");
            }
            
            boolean success = logisticsService.updateLogisticsStatus(id, status);
            if (success) {
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新当前位置
     */
    @PutMapping("/{id}/location")
    public Result<String> updateCurrentLocation(@PathVariable Long id, @RequestBody Map<String, String> locationData) {
        try {
            String location = locationData.get("location");
            if (location == null || location.isEmpty()) {
                return Result.error("位置不能为空");
            }
            
            boolean success = logisticsService.updateCurrentLocation(id, location);
            if (success) {
                return Result.success("位置更新成功");
            } else {
                return Result.error("位置更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新位置失败: " + e.getMessage());
        }
    }
    
    /**
     * 发货
     */
    @PostMapping("/ship")
    public Result<String> shipOrder(@RequestBody Map<String, Object> shipData) {
        try {
            Long orderId = Long.valueOf(shipData.get("orderId").toString());
            String company = shipData.get("company").toString();
            String trackingNo = shipData.get("trackingNo").toString();
            String remark = shipData.get("remark") != null ? shipData.get("remark").toString() : "";
            
            boolean success = logisticsService.shipOrder(orderId, company, trackingNo, remark);
            if (success) {
                return Result.success("订单发货成功");
            } else {
                return Result.error("订单发货失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发货失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记为运输中
     */
    @PostMapping("/{id}/transit")
    public Result<String> markInTransit(@PathVariable Long id, @RequestBody Map<String, String> locationData) {
        try {
            String location = locationData.get("location");
            if (location == null || location.isEmpty()) {
                return Result.error("当前位置不能为空");
            }
            
            boolean success = logisticsService.markInTransit(id, location);
            if (success) {
                return Result.success("已标记为运输中");
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记运输中失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记为已到达
     */
    @PostMapping("/{id}/arrived")
    public Result<String> markArrived(@PathVariable Long id, @RequestBody Map<String, String> locationData) {
        try {
            String location = locationData.get("location");
            if (location == null || location.isEmpty()) {
                return Result.error("当前位置不能为空");
            }
            
            boolean success = logisticsService.markArrived(id, location);
            if (success) {
                return Result.success("已标记为到达");
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记到达失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记为已送达
     */
    @PostMapping("/{id}/delivered")
    public Result<String> markDelivered(@PathVariable Long id) {
        try {
            boolean success = logisticsService.markDelivered(id);
            if (success) {
                return Result.success("已标记为送达");
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记送达失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除物流信息
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteLogistics(@PathVariable Long id) {
        try {
            boolean success = logisticsService.deleteLogistics(id);
            if (success) {
                return Result.success("物流信息删除成功");
            } else {
                return Result.error("物流信息删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除物流信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取物流统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getLogisticsStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", logisticsService.countAllLogistics());
            statistics.put("shipped", logisticsService.countLogisticsByStatus("SHIPPED"));
            statistics.put("inTransit", logisticsService.countLogisticsByStatus("IN_TRANSIT"));
            statistics.put("arrived", logisticsService.countLogisticsByStatus("ARRIVED"));
            statistics.put("delivered", logisticsService.countLogisticsByStatus("DELIVERED"));
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
}
