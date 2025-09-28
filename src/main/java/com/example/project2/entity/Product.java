package com.example.project2.entity;

import java.math.BigDecimal;

/**
 * 养殖产品实体类
 */
public class Product extends BaseEntity {
    /**
     * 产品ID
     */
    private Long id;
    
    /**
     * 产品名称
     */
    private String name;
    
    /**
     * 品种
     */
    private String variety;
    
    /**
     * 规格
     */
    private String specification;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 库存数量
     */
    private Integer stockQuantity;
    
    /**
     * 养殖周期（天）
     */
    private Integer breedingCycle;
    
    /**
     * 产品描述
     */
    private String description;
    
    /**
     * 销售方ID
     */
    private Long sellerId;
    
    /**
     * 产品图片URL
     */
    private String imageUrl;
    
    /**
     * 状态：1-上架，0-下架
     */
    private Integer status;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVariety() {
        return variety;
    }
    
    public void setVariety(String variety) {
        this.variety = variety;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Integer getBreedingCycle() {
        return breedingCycle;
    }
    
    public void setBreedingCycle(Integer breedingCycle) {
        this.breedingCycle = breedingCycle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
}
