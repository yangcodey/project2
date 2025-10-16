-- =============================================
-- 补充数据库表 - 养殖计划和物流配送
-- =============================================

USE nongbangwoyang;

-- 1. 养殖计划表
CREATE TABLE IF NOT EXISTS breeding_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_no VARCHAR(50) NOT NULL UNIQUE COMMENT '计划编号',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    planned_quantity INT NOT NULL COMMENT '计划养殖数量',
    start_date DATE COMMENT '养殖开始时间',
    expected_date DATE COMMENT '预计出栏时间',
    breeding_site VARCHAR(255) COMMENT '养殖场地',
    breeding_method VARCHAR(100) COMMENT '养殖方式',
    manager_id BIGINT COMMENT '负责人ID',
    status ENUM('PLANNING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PLANNING' COMMENT '计划状态',
    description TEXT COMMENT '计划描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id),
    INDEX idx_manager_id (manager_id),
    INDEX idx_status (status),
    INDEX idx_start_date (start_date),
    INDEX idx_expected_date (expected_date),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='养殖计划表';

-- 2. 物流配送表
CREATE TABLE IF NOT EXISTS logistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    logistics_no VARCHAR(50) NOT NULL UNIQUE COMMENT '物流编号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    company VARCHAR(100) NOT NULL COMMENT '物流公司',
    tracking_no VARCHAR(100) NOT NULL COMMENT '物流单号',
    ship_time TIMESTAMP NULL COMMENT '发货时间',
    expected_arrival TIMESTAMP NULL COMMENT '预计到达时间',
    status ENUM('SHIPPED', 'IN_TRANSIT', 'ARRIVED', 'DELIVERED') DEFAULT 'SHIPPED' COMMENT '物流状态',
    current_location VARCHAR(255) COMMENT '当前位置',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_tracking_no (tracking_no),
    INDEX idx_status (status),
    INDEX idx_company (company),
    INDEX idx_ship_time (ship_time),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流配送表';

-- 3. 插入示例数据 - 养殖计划
INSERT INTO breeding_plans (id, plan_no, product_id, planned_quantity, start_date, expected_date, breeding_site, breeding_method, manager_id, status, description) VALUES
(1, 'BP20240101A001', 1, 500, '2024-01-01', '2024-07-01', '张三农场A区', '散养', 2, 'IN_PROGRESS', '春季散养土鸡养殖计划'),
(2, 'BP20240102A002', 3, 200, '2024-01-15', '2024-12-15', '李四养殖场B区', '圈养', 3, 'PLANNING', '黑猪养殖计划'),
(3, 'BP20240103A003', 5, 50, '2024-02-01', '2025-02-01', '张三农场C区', '放养', 2, 'PLANNING', '高山生态羊养殖计划');

-- 4. 插入示例数据 - 物流配送
INSERT INTO logistics (id, logistics_no, order_id, company, tracking_no, ship_time, expected_arrival, status, current_location, remark) VALUES
(1, 'LG20240101001', 1, '顺丰速运', 'SF1234567890', '2024-01-02 10:00:00', '2024-01-03 18:00:00', 'DELIVERED', '北京市朝阳区', '已签收'),
(2, 'LG20240102002', 2, '中通快递', 'ZT9876543210', '2024-01-02 14:00:00', '2024-01-04 12:00:00', 'IN_TRANSIT', '河北省石家庄市', '运输中');

-- 5. 完成提示
SELECT '补充数据库表创建完成！' as message;
SELECT COUNT(*) as breeding_plan_count FROM breeding_plans;
SELECT COUNT(*) as logistics_count FROM logistics;

