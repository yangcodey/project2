package com.example.project2.entity;

import java.time.LocalDateTime;

/**
 * 物流信息实体类
 */
public class Logistics extends BaseEntity {
    /**
     * 物流ID
     */
    private Long id;
    
    /**
     * 物流编号
     */
    private String logisticsNo;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 物流公司
     */
    private String company;
    
    /**
     * 物流单号
     */
    private String trackingNo;
    
    /**
     * 发货时间
     */
    private LocalDateTime shipTime;
    
    /**
     * 预计到达时间
     */
    private LocalDateTime expectedArrival;
    
    /**
     * 物流状态
     */
    private String status;
    
    /**
     * 当前位置
     */
    private String currentLocation;
    
    /**
     * 备注
     */
    private String remark;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLogisticsNo() {
        return logisticsNo;
    }
    
    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getTrackingNo() {
        return trackingNo;
    }
    
    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }
    
    public LocalDateTime getShipTime() {
        return shipTime;
    }
    
    public void setShipTime(LocalDateTime shipTime) {
        this.shipTime = shipTime;
    }
    
    public LocalDateTime getExpectedArrival() {
        return expectedArrival;
    }
    
    public void setExpectedArrival(LocalDateTime expectedArrival) {
        this.expectedArrival = expectedArrival;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
