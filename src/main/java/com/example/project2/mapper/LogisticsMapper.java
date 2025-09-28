package com.example.project2.mapper;

import com.example.project2.entity.Logistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 物流Mapper接口
 */
@Mapper
public interface LogisticsMapper {
    
    /**
     * 根据ID查询物流信息
     */
    Logistics selectById(@Param("id") Long id);
    
    /**
     * 查询所有物流信息
     */
    List<Logistics> selectAll();
    
    /**
     * 根据订单ID查询物流信息
     */
    List<Logistics> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据物流编号查询物流信息
     */
    Logistics selectByLogisticsNo(@Param("logisticsNo") String logisticsNo);
    
    /**
     * 根据物流单号查询物流信息
     */
    Logistics selectByTrackingNo(@Param("trackingNo") String trackingNo);
    
    /**
     * 根据状态查询物流信息
     */
    List<Logistics> selectByStatus(@Param("status") String status);
    
    /**
     * 根据物流公司查询物流信息
     */
    List<Logistics> selectByCompany(@Param("company") String company);
    
    /**
     * 插入物流信息
     */
    int insert(Logistics logistics);
    
    /**
     * 更新物流信息
     */
    int update(Logistics logistics);
    
    /**
     * 更新物流状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 更新当前位置
     */
    int updateCurrentLocation(@Param("id") Long id, @Param("location") String location);
    
    /**
     * 根据ID删除物流信息
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 统计物流信息数量
     */
    int countByStatus(@Param("status") String status);
    
    /**
     * 统计总物流数量
     */
    int countAll();
    
    /**
     * 获取物流公司分布
     */
    List<Map<String, Object>> getCompanyDistribution();
}
