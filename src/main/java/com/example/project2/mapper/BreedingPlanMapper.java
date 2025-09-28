package com.example.project2.mapper;

import com.example.project2.entity.BreedingPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 养殖计划Mapper接口
 */
@Mapper
public interface BreedingPlanMapper {
    
    /**
     * 根据ID查询养殖计划
     */
    BreedingPlan selectById(@Param("id") Long id);
    
    /**
     * 查询所有养殖计划
     */
    List<BreedingPlan> selectAll();
    
    /**
     * 根据产品ID查询养殖计划
     */
    List<BreedingPlan> selectByProductId(@Param("productId") Long productId);
    
    /**
     * 根据负责人ID查询养殖计划
     */
    List<BreedingPlan> selectByManagerId(@Param("managerId") Long managerId);
    
    /**
     * 根据状态查询养殖计划
     */
    List<BreedingPlan> selectByStatus(@Param("status") String status);
    
    /**
     * 根据计划编号查询养殖计划
     */
    BreedingPlan selectByPlanNo(@Param("planNo") String planNo);
    
    /**
     * 插入养殖计划
     */
    int insert(BreedingPlan breedingPlan);
    
    /**
     * 更新养殖计划
     */
    int update(BreedingPlan breedingPlan);
    
    /**
     * 更新养殖计划状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 根据ID删除养殖计划
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 统计养殖计划数量
     */
    int countByStatus(@Param("status") String status);
    
    /**
     * 统计总计划数量
     */
    int countAll();
    
    /**
     * 获取总计划数量
     */
    int getTotalPlannedQuantity();
    
    /**
     * 获取平均计划数量
     */
    int getAveragePlannedQuantity();
}
