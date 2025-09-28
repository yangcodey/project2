package com.example.project2.service;

import com.example.project2.entity.BreedingPlan;
import com.example.project2.mapper.BreedingPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 养殖计划服务类
 */
@Service
public class BreedingPlanService {
    
    @Autowired
    private BreedingPlanMapper breedingPlanMapper;
    
    /**
     * 根据ID查询养殖计划
     */
    public BreedingPlan getBreedingPlanById(Long id) {
        return breedingPlanMapper.selectById(id);
    }
    
    /**
     * 查询所有养殖计划
     */
    public List<BreedingPlan> getAllBreedingPlans() {
        return breedingPlanMapper.selectAll();
    }
    
    /**
     * 根据产品ID查询养殖计划
     */
    public List<BreedingPlan> getBreedingPlansByProductId(Long productId) {
        return breedingPlanMapper.selectByProductId(productId);
    }
    
    /**
     * 根据负责人ID查询养殖计划
     */
    public List<BreedingPlan> getBreedingPlansByManagerId(Long managerId) {
        return breedingPlanMapper.selectByManagerId(managerId);
    }
    
    /**
     * 根据状态查询养殖计划
     */
    public List<BreedingPlan> getBreedingPlansByStatus(String status) {
        return breedingPlanMapper.selectByStatus(status);
    }
    
    /**
     * 根据计划编号查询养殖计划
     */
    public BreedingPlan getBreedingPlanByPlanNo(String planNo) {
        return breedingPlanMapper.selectByPlanNo(planNo);
    }
    
    /**
     * 添加养殖计划
     */
    public boolean addBreedingPlan(BreedingPlan breedingPlan) {
        // 生成计划编号
        if (breedingPlan.getPlanNo() == null || breedingPlan.getPlanNo().isEmpty()) {
            breedingPlan.setPlanNo(generatePlanNo());
        }
        
        // 设置默认状态
        if (breedingPlan.getStatus() == null || breedingPlan.getStatus().isEmpty()) {
            breedingPlan.setStatus("PLANNING");
        }
        
        return breedingPlanMapper.insert(breedingPlan) > 0;
    }
    
    /**
     * 更新养殖计划
     */
    public boolean updateBreedingPlan(BreedingPlan breedingPlan) {
        return breedingPlanMapper.update(breedingPlan) > 0;
    }
    
    /**
     * 更新养殖计划状态
     */
    public boolean updateBreedingPlanStatus(Long id, String status) {
        return breedingPlanMapper.updateStatus(id, status) > 0;
    }
    
    /**
     * 删除养殖计划
     */
    public boolean deleteBreedingPlan(Long id) {
        return breedingPlanMapper.deleteById(id) > 0;
    }
    
    /**
     * 统计养殖计划数量
     */
    public int countBreedingPlansByStatus(String status) {
        return breedingPlanMapper.countByStatus(status);
    }
    
    /**
     * 统计总计划数量
     */
    public int countAllBreedingPlans() {
        return breedingPlanMapper.countAll();
    }
    
    /**
     * 生成计划编号
     */
    private String generatePlanNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "BP" + dateStr + uuid;
    }
    
    /**
     * 开始执行养殖计划
     */
    public boolean startBreedingPlan(Long id) {
        BreedingPlan plan = breedingPlanMapper.selectById(id);
        if (plan != null && "PLANNING".equals(plan.getStatus())) {
            return breedingPlanMapper.updateStatus(id, "IN_PROGRESS") > 0;
        }
        return false;
    }
    
    /**
     * 完成养殖计划
     */
    public boolean completeBreedingPlan(Long id) {
        BreedingPlan plan = breedingPlanMapper.selectById(id);
        if (plan != null && "IN_PROGRESS".equals(plan.getStatus())) {
            return breedingPlanMapper.updateStatus(id, "COMPLETED") > 0;
        }
        return false;
    }
    
    /**
     * 取消养殖计划
     */
    public boolean cancelBreedingPlan(Long id) {
        BreedingPlan plan = breedingPlanMapper.selectById(id);
        if (plan != null && !"COMPLETED".equals(plan.getStatus())) {
            return breedingPlanMapper.updateStatus(id, "CANCELLED") > 0;
        }
        return false;
    }
}
