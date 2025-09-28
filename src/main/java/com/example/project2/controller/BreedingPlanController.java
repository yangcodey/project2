package com.example.project2.controller;

import com.example.project2.common.Result;
import com.example.project2.entity.BreedingPlan;
import com.example.project2.service.BreedingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 养殖计划控制器
 */
@RestController
@RequestMapping("/api/breeding-plans")
@CrossOrigin(origins = "*")
public class BreedingPlanController {
    
    @Autowired
    private BreedingPlanService breedingPlanService;
    
    /**
     * 获取所有养殖计划
     */
    @GetMapping
    public Result<List<BreedingPlan>> getAllBreedingPlans() {
        try {
            List<BreedingPlan> plans = breedingPlanService.getAllBreedingPlans();
            return Result.success(plans);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取养殖计划列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取养殖计划
     */
    @GetMapping("/{id}")
    public Result<BreedingPlan> getBreedingPlanById(@PathVariable Long id) {
        try {
            BreedingPlan plan = breedingPlanService.getBreedingPlanById(id);
            if (plan != null) {
                return Result.success(plan);
            } else {
                return Result.error("养殖计划不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取养殖计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据产品ID获取养殖计划
     */
    @GetMapping("/product/{productId}")
    public Result<List<BreedingPlan>> getBreedingPlansByProductId(@PathVariable Long productId) {
        try {
            List<BreedingPlan> plans = breedingPlanService.getBreedingPlansByProductId(productId);
            return Result.success(plans);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品养殖计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据负责人ID获取养殖计划
     */
    @GetMapping("/manager/{managerId}")
    public Result<List<BreedingPlan>> getBreedingPlansByManagerId(@PathVariable Long managerId) {
        try {
            List<BreedingPlan> plans = breedingPlanService.getBreedingPlansByManagerId(managerId);
            return Result.success(plans);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取负责人养殖计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据状态获取养殖计划
     */
    @GetMapping("/status/{status}")
    public Result<List<BreedingPlan>> getBreedingPlansByStatus(@PathVariable String status) {
        try {
            List<BreedingPlan> plans = breedingPlanService.getBreedingPlansByStatus(status);
            return Result.success(plans);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取状态养殖计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加养殖计划
     */
    @PostMapping
    public Result<String> addBreedingPlan(@RequestBody BreedingPlan breedingPlan) {
        try {
            if (breedingPlan.getProductId() == null || breedingPlan.getPlannedQuantity() == null || 
                breedingPlan.getStartDate() == null || breedingPlan.getExpectedDate() == null || 
                breedingPlan.getManagerId() == null) {
                return Result.error("产品ID、计划数量、开始时间、预计时间和负责人不能为空");
            }
            
            boolean success = breedingPlanService.addBreedingPlan(breedingPlan);
            if (success) {
                return Result.success("养殖计划添加成功");
            } else {
                return Result.error("养殖计划添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加养殖计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新养殖计划
     */
    @PutMapping("/{id}")
    public Result<String> updateBreedingPlan(@PathVariable Long id, @RequestBody BreedingPlan breedingPlan) {
        try {
            breedingPlan.setId(id);
            boolean success = breedingPlanService.updateBreedingPlan(breedingPlan);
            if (success) {
                return Result.success("养殖计划更新成功");
            } else {
                return Result.error("养殖计划更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新养殖计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新养殖计划状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateBreedingPlanStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            if (status == null || status.isEmpty()) {
                return Result.error("状态不能为空");
            }
            
            boolean success = breedingPlanService.updateBreedingPlanStatus(id, status);
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
     * 开始执行养殖计划
     */
    @PostMapping("/{id}/start")
    public Result<String> startBreedingPlan(@PathVariable Long id) {
        try {
            boolean success = breedingPlanService.startBreedingPlan(id);
            if (success) {
                return Result.success("养殖计划开始执行");
            } else {
                return Result.error("开始执行失败，请检查计划状态");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("开始执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 完成养殖计划
     */
    @PostMapping("/{id}/complete")
    public Result<String> completeBreedingPlan(@PathVariable Long id) {
        try {
            boolean success = breedingPlanService.completeBreedingPlan(id);
            if (success) {
                return Result.success("养殖计划已完成");
            } else {
                return Result.error("完成计划失败，请检查计划状态");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("完成计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消养殖计划
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancelBreedingPlan(@PathVariable Long id) {
        try {
            boolean success = breedingPlanService.cancelBreedingPlan(id);
            if (success) {
                return Result.success("养殖计划已取消");
            } else {
                return Result.error("取消计划失败，请检查计划状态");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("取消计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除养殖计划
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteBreedingPlan(@PathVariable Long id) {
        try {
            boolean success = breedingPlanService.deleteBreedingPlan(id);
            if (success) {
                return Result.success("养殖计划删除成功");
            } else {
                return Result.error("养殖计划删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除养殖计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取养殖计划统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getBreedingPlanStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", breedingPlanService.countAllBreedingPlans());
            statistics.put("planning", breedingPlanService.countBreedingPlansByStatus("PLANNING"));
            statistics.put("inProgress", breedingPlanService.countBreedingPlansByStatus("IN_PROGRESS"));
            statistics.put("completed", breedingPlanService.countBreedingPlansByStatus("COMPLETED"));
            statistics.put("cancelled", breedingPlanService.countBreedingPlansByStatus("CANCELLED"));
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
}
