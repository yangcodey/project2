package com.example.project2.entity;

import java.time.LocalDate;

/**
 * 养殖计划实体类
 */
public class BreedingPlan extends BaseEntity {
    /**
     * 计划ID
     */
    private Long id;
    
    /**
     * 计划编号
     */
    private String planNo;
    
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 计划养殖数量
     */
    private Integer plannedQuantity;
    
    /**
     * 养殖开始时间
     */
    private LocalDate startDate;
    
    /**
     * 预计出栏时间
     */
    private LocalDate expectedDate;
    
    /**
     * 养殖场地
     */
    private String breedingSite;
    
    /**
     * 养殖方式
     */
    private String breedingMethod;
    
    /**
     * 负责人ID
     */
    private Long managerId;
    
    /**
     * 计划状态
     */
    private String status;
    
    /**
     * 计划描述
     */
    private String description;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPlanNo() {
        return planNo;
    }
    
    public void setPlanNo(String planNo) {
        this.planNo = planNo;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getPlannedQuantity() {
        return plannedQuantity;
    }
    
    public void setPlannedQuantity(Integer plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getExpectedDate() {
        return expectedDate;
    }
    
    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }
    
    public String getBreedingSite() {
        return breedingSite;
    }
    
    public void setBreedingSite(String breedingSite) {
        this.breedingSite = breedingSite;
    }
    
    public String getBreedingMethod() {
        return breedingMethod;
    }
    
    public void setBreedingMethod(String breedingMethod) {
        this.breedingMethod = breedingMethod;
    }
    
    public Long getManagerId() {
        return managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
