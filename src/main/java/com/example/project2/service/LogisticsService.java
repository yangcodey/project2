package com.example.project2.service;

import com.example.project2.entity.Logistics;
import com.example.project2.mapper.LogisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 物流服务类
 */
@Service
public class LogisticsService {
    
    @Autowired
    private LogisticsMapper logisticsMapper;
    
    /**
     * 根据ID查询物流信息
     */
    public Logistics getLogisticsById(Long id) {
        return logisticsMapper.selectById(id);
    }
    
    /**
     * 查询所有物流信息
     */
    public List<Logistics> getAllLogistics() {
        return logisticsMapper.selectAll();
    }
    
    /**
     * 根据订单ID查询物流信息
     */
    public List<Logistics> getLogisticsByOrderId(Long orderId) {
        return logisticsMapper.selectByOrderId(orderId);
    }
    
    /**
     * 根据物流编号查询物流信息
     */
    public Logistics getLogisticsByLogisticsNo(String logisticsNo) {
        return logisticsMapper.selectByLogisticsNo(logisticsNo);
    }
    
    /**
     * 根据物流单号查询物流信息
     */
    public Logistics getLogisticsByTrackingNo(String trackingNo) {
        return logisticsMapper.selectByTrackingNo(trackingNo);
    }
    
    /**
     * 根据状态查询物流信息
     */
    public List<Logistics> getLogisticsByStatus(String status) {
        return logisticsMapper.selectByStatus(status);
    }
    
    /**
     * 根据物流公司查询物流信息
     */
    public List<Logistics> getLogisticsByCompany(String company) {
        return logisticsMapper.selectByCompany(company);
    }
    
    /**
     * 添加物流信息
     */
    public boolean addLogistics(Logistics logistics) {
        // 生成物流编号
        if (logistics.getLogisticsNo() == null || logistics.getLogisticsNo().isEmpty()) {
            logistics.setLogisticsNo(generateLogisticsNo());
        }
        
        // 设置默认状态
        if (logistics.getStatus() == null || logistics.getStatus().isEmpty()) {
            logistics.setStatus("SHIPPED");
        }
        
        // 设置发货时间
        if (logistics.getShipTime() == null) {
            logistics.setShipTime(LocalDateTime.now());
        }
        
        return logisticsMapper.insert(logistics) > 0;
    }
    
    /**
     * 更新物流信息
     */
    public boolean updateLogistics(Logistics logistics) {
        return logisticsMapper.update(logistics) > 0;
    }
    
    /**
     * 更新物流状态
     */
    public boolean updateLogisticsStatus(Long id, String status) {
        return logisticsMapper.updateStatus(id, status) > 0;
    }
    
    /**
     * 更新当前位置
     */
    public boolean updateCurrentLocation(Long id, String location) {
        return logisticsMapper.updateCurrentLocation(id, location) > 0;
    }
    
    /**
     * 删除物流信息
     */
    public boolean deleteLogistics(Long id) {
        return logisticsMapper.deleteById(id) > 0;
    }
    
    /**
     * 统计物流信息数量
     */
    public int countLogisticsByStatus(String status) {
        return logisticsMapper.countByStatus(status);
    }
    
    /**
     * 统计总物流数量
     */
    public int countAllLogistics() {
        return logisticsMapper.countAll();
    }
    
    /**
     * 生成物流编号
     */
    private String generateLogisticsNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "LG" + dateStr + uuid;
    }
    
    /**
     * 发货
     */
    public boolean shipOrder(Long orderId, String company, String trackingNo, String remark) {
        Logistics logistics = new Logistics();
        logistics.setOrderId(orderId);
        logistics.setCompany(company);
        logistics.setTrackingNo(trackingNo);
        logistics.setStatus("SHIPPED");
        logistics.setShipTime(LocalDateTime.now());
        logistics.setRemark(remark);
        
        return addLogistics(logistics);
    }
    
    /**
     * 更新物流状态为运输中
     */
    public boolean markInTransit(Long id, String currentLocation) {
        Logistics logistics = new Logistics();
        logistics.setId(id);
        logistics.setStatus("IN_TRANSIT");
        logistics.setCurrentLocation(currentLocation);
        
        return updateLogistics(logistics);
    }
    
    /**
     * 更新物流状态为已到达
     */
    public boolean markArrived(Long id, String currentLocation) {
        Logistics logistics = new Logistics();
        logistics.setId(id);
        logistics.setStatus("ARRIVED");
        logistics.setCurrentLocation(currentLocation);
        
        return updateLogistics(logistics);
    }
    
    /**
     * 更新物流状态为已送达
     */
    public boolean markDelivered(Long id) {
        return updateLogisticsStatus(id, "DELIVERED");
    }
    
    /**
     * 获取物流跟踪信息
     */
    public Logistics getLogisticsTracking(String trackingNo) {
        return getLogisticsByTrackingNo(trackingNo);
    }
}
