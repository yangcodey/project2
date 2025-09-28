

-- 创建数据库
CREATE DATABASE IF NOT EXISTS nongbangwoyang CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nongbangwoyang;

-- ==============================================
-- 1. 基础表结构
-- ==============================================

-- 1.1 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    address TEXT COMMENT '地址',
    avatar VARCHAR(255) COMMENT '头像URL',
    role ENUM('ADMIN', 'SELLER', 'BUYER') NOT NULL DEFAULT 'BUYER' COMMENT '角色：管理员、销售方、购买方',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 1.2 产品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '产品名称',
    variety VARCHAR(100) NOT NULL COMMENT '品种',
    specification VARCHAR(100) COMMENT '规格',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    breeding_cycle INT COMMENT '养殖周期（天）',
    description TEXT COMMENT '产品描述',
    seller_id BIGINT NOT NULL COMMENT '销售方ID',
    image_url VARCHAR(255) COMMENT '产品图片URL',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：1-上架，0-下架',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '产品表';

-- 1.3 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    buyer_id BIGINT NOT NULL COMMENT '购买方ID',
    seller_id BIGINT NOT NULL COMMENT '销售方ID',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
    quantity INT NOT NULL COMMENT '购买数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_time TIMESTAMP NULL COMMENT '支付时间',
    delivery_address TEXT NOT NULL COMMENT '收货地址',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') 
           DEFAULT 'PENDING' COMMENT '订单状态',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '订单表';

-- ==============================================
-- 2. 商城功能表
-- ==============================================

-- 2.1 产品分类表
CREATE TABLE IF NOT EXISTS product_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level INT DEFAULT 1 COMMENT '分类层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    icon VARCHAR(200) COMMENT '分类图标',
    description TEXT COMMENT '分类描述',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent (parent_id),
    INDEX idx_level (level),
    INDEX idx_sort (sort_order),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类表';

-- 2.2 产品供应商关系表
CREATE TABLE IF NOT EXISTS product_suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '产品ID',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID (销售方用户ID)',
    supplier_price DECIMAL(10,2) NOT NULL COMMENT '供应商价格',
    min_order_quantity INT DEFAULT 1 COMMENT '最小订购量',
    max_order_quantity INT DEFAULT 9999 COMMENT '最大订购量',
    lead_time INT DEFAULT 7 COMMENT '交货周期(天)',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_product_supplier (product_id, supplier_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_product (product_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品供应商关系表';

-- 2.3 销售方库存表
CREATE TABLE IF NOT EXISTS seller_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id BIGINT NOT NULL COMMENT '销售方ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    available_quantity INT DEFAULT 0 COMMENT '可用库存',
    reserved_quantity INT DEFAULT 0 COMMENT '预留库存',
    total_quantity INT DEFAULT 0 COMMENT '总库存',
    min_stock_level INT DEFAULT 0 COMMENT '最低库存警戒线',
    max_stock_level INT DEFAULT 9999 COMMENT '最高库存',
    cost_price DECIMAL(10,2) DEFAULT 0 COMMENT '成本价',
    selling_price DECIMAL(10,2) DEFAULT 0 COMMENT '销售价',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_seller_product (seller_id, product_id),
    INDEX idx_seller (seller_id),
    INDEX idx_product (product_id),
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售方库存表';

-- 2.4 商城产品表
CREATE TABLE IF NOT EXISTS marketplace_products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '产品ID',
    seller_id BIGINT NOT NULL COMMENT '销售方ID',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    images JSON COMMENT '商品图片(JSON数组)',
    category_id BIGINT COMMENT '分类ID',
    tags VARCHAR(500) COMMENT '标签(逗号分隔)',
    is_featured TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
    is_hot TINYINT(1) DEFAULT 0 COMMENT '是否热门',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product (product_id),
    INDEX idx_seller (seller_id),
    INDEX idx_category (category_id),
    INDEX idx_featured (is_featured),
    INDEX idx_hot (is_hot),
    INDEX idx_active (is_active),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城产品表';

-- 2.5 销售方采购订单表
CREATE TABLE IF NOT EXISTS seller_purchase_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '采购订单号',
    buyer_id BIGINT NOT NULL COMMENT '采购方ID (销售方用户ID)',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID (销售方用户ID)',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    quantity INT NOT NULL COMMENT '采购数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '订单状态',
    delivery_address VARCHAR(500) COMMENT '收货地址',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    notes TEXT COMMENT '备注',
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    confirmed_time TIMESTAMP NULL COMMENT '确认时间',
    shipped_time TIMESTAMP NULL COMMENT '发货时间',
    delivered_time TIMESTAMP NULL COMMENT '送达时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_buyer (buyer_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_product (product_id),
    INDEX idx_status (status),
    INDEX idx_order_time (order_time),
    FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售方采购订单表';

-- 2.6 销售方评价表
CREATE TABLE IF NOT EXISTS seller_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reviewer_id BIGINT NOT NULL COMMENT '评价方ID',
    reviewed_id BIGINT NOT NULL COMMENT '被评价方ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    rating TINYINT NOT NULL COMMENT '评分(1-5)',
    content TEXT COMMENT '评价内容',
    images JSON COMMENT '评价图片(JSON数组)',
    is_anonymous TINYINT(1) DEFAULT 0 COMMENT '是否匿名',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_reviewer (reviewer_id),
    INDEX idx_reviewed (reviewed_id),
    INDEX idx_order (order_id),
    INDEX idx_rating (rating),
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES seller_purchase_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售方评价表';

-- ==============================================
-- 3. 个人中心功能表
-- ==============================================

-- 3.1 用户地址表
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    address VARCHAR(255) NOT NULL COMMENT '详细地址',
    postal_code VARCHAR(10) COMMENT '邮政编码',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 3.2 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(20) NOT NULL COMMENT '收藏类型 (PRODUCT, SELLER)',
    target_id BIGINT NOT NULL COMMENT '目标ID (产品ID或销售方ID)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_type_target (user_id, type, target_id),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 3.3 浏览历史表
CREATE TABLE IF NOT EXISTS browse_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    browse_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_browse_time (browse_time),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- ==============================================
-- 4. 通知系统表
-- ==============================================

-- 4.1 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type VARCHAR(50) NOT NULL COMMENT '通知类型 (ORDER, SYSTEM, PROMOTION, STOCK)',
    title VARCHAR(255) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    status VARCHAR(20) DEFAULT 'UNREAD' COMMENT '通知状态 (UNREAD, READ, DELETED)',
    priority VARCHAR(20) DEFAULT 'NORMAL' COMMENT '优先级 (LOW, NORMAL, HIGH, URGENT)',
    action_url VARCHAR(500) COMMENT '操作链接',
    action_text VARCHAR(100) COMMENT '操作按钮文字',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_at TIMESTAMP NULL COMMENT '阅读时间',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_type (type),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户通知表';

-- ==============================================
-- 5. 产品评价表
-- ==============================================

-- 5.1 产品评价表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '产品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_id BIGINT COMMENT '订单ID (可选)',
    rating INT NOT NULL COMMENT '评分 (1-5星)',
    comment TEXT COMMENT '评价内容',
    image_urls JSON COMMENT '评价图片URL列表 (JSON数组)',
    likes INT DEFAULT 0 COMMENT '点赞数',
    status INT DEFAULT 1 COMMENT '评价状态 (1:正常, 0:已删除, 2:待审核)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品评价表';

-- ==============================================
-- 6. 插入初始数据
-- ==============================================

-- 6.1 插入用户数据
INSERT INTO users (id, username, password, real_name, phone, email, role, status) VALUES
(1, 'admin', '1234', '系统管理员', '13800138000', 'admin@example.com', 'ADMIN', 1),
(2, 'seller1', '1234', '张三农场', '13800138001', 'seller1@example.com', 'SELLER', 1),
(3, 'seller2', '1234', '李四养殖场', '13800138002', 'seller2@example.com', 'SELLER', 1),
(4, 'buyer1', '1234', '王五', '13800138003', 'buyer1@example.com', 'BUYER', 1),
(5, 'buyer2', '1234', '赵六', '13800138004', 'buyer2@example.com', 'BUYER', 1);

-- 6.2 插入产品数据
INSERT INTO products (id, name, variety, specification, price, stock_quantity, breeding_cycle, description, seller_id, image_url, status) VALUES
(1, '散养土鸡', '鸡', '2.5kg/只', 68.00, 50, 180, '山林散养，肉质紧实，味道鲜美。', 2, 'chicken.jpg', 1),
(2, '生态鲜鸡蛋', '蛋', '30枚/盒', 45.00, 200, 0, '农场直供，天然有机，蛋香浓郁。', 2, 'eggs.jpg', 1),
(3, '生态黑猪肉', '猪', '500g/份', 88.00, 120, 300, '农家自养黑猪，肥而不腻，口感醇厚。', 3, 'pork.jpg', 1),
(4, '散养土鸭', '鸭', '2kg/只', 78.00, 30, 150, '水边散养，肉质鲜美，营养价值高。', 3, 'duck.jpg', 1),
(5, '高山生态羊', '羊', '整羊/半羊', 1200.00, 10, 365, '高山放养，肉质细嫩，营养丰富。', 2, 'lamb.jpg', 1);

-- 6.3 插入产品分类数据
INSERT INTO product_categories (id, name, parent_id, level, sort_order, icon, description, is_active) VALUES
(1, '家禽类', 0, 1, 1, 'chicken-icon', '各种家禽产品', 1),
(2, '家畜类', 0, 1, 2, 'pig-icon', '各种家畜产品', 1),
(3, '蛋类', 0, 1, 3, 'egg-icon', '各种蛋类产品', 1),
(4, '水产类', 0, 1, 4, 'fish-icon', '各种水产产品', 1),
(5, '土鸡', 1, 2, 1, 'chicken-icon', '散养土鸡', 1),
(6, '肉鸡', 1, 2, 2, 'chicken-icon', '肉用鸡', 1),
(7, '土猪', 2, 2, 1, 'pig-icon', '散养土猪', 1),
(8, '黑猪', 2, 2, 2, 'pig-icon', '生态黑猪', 1),
(9, '鸡蛋', 3, 2, 1, 'egg-icon', '各种鸡蛋', 1),
(10, '鸭蛋', 3, 2, 2, 'egg-icon', '各种鸭蛋', 1);

-- 6.4 插入产品供应商关系数据
INSERT INTO product_suppliers (product_id, supplier_id, supplier_price, min_order_quantity, max_order_quantity, lead_time, is_active) VALUES
(1, 2, 45.00, 10, 1000, 3, 1),
(1, 3, 48.00, 5, 500, 5, 1),
(2, 2, 35.00, 20, 2000, 2, 1),
(2, 3, 38.00, 10, 1000, 3, 1),
(3, 2, 60.00, 5, 200, 7, 1),
(3, 3, 65.00, 3, 100, 10, 1),
(4, 3, 55.00, 5, 300, 4, 1),
(5, 2, 800.00, 1, 50, 14, 1);

-- 6.5 插入销售方库存数据
INSERT INTO seller_inventory (seller_id, product_id, available_quantity, reserved_quantity, total_quantity, min_stock_level, max_stock_level, cost_price, selling_price, is_active) VALUES
(2, 1, 50, 0, 50, 10, 200, 45.00, 68.00, 1),
(2, 2, 200, 0, 200, 20, 500, 35.00, 45.00, 1),
(3, 3, 120, 0, 120, 10, 300, 60.00, 88.00, 1),
(3, 4, 30, 0, 30, 5, 100, 55.00, 78.00, 1),
(2, 5, 10, 0, 10, 2, 50, 800.00, 1200.00, 1);

-- 6.6 插入订单数据
INSERT INTO orders (id, order_no, product_id, buyer_id, seller_id, product_name, quantity, unit_price, total_amount, status, delivery_address, contact_phone, created_at) VALUES
(1, 'ORD202401010001', 1, 4, 2, '散养土鸡', 2, 68.00, 136.00, 'DELIVERED', '北京市朝阳区某某小区1号楼101室', '13800138003', '2024-01-01 11:00:00'),
(2, 'ORD202401010002', 2, 5, 2, '生态鲜鸡蛋', 3, 45.00, 135.00, 'SHIPPED', '上海市浦东新区某某路123号', '13800138004', '2024-01-01 12:00:00'),
(3, 'ORD202401010003', 3, 4, 3, '生态黑猪肉', 1, 88.00, 88.00, 'PENDING', '北京市朝阳区某某小区1号楼101室', '13800138003', '2024-01-01 13:00:00');

-- 6.7 插入通知数据
INSERT INTO notifications (id, user_id, type, title, content, status, priority, action_url, action_text, created_at) VALUES
(1, 1, 'SYSTEM', '欢迎使用侬帮我养系统', '欢迎您使用侬帮我养农产品销售系统！', 'UNREAD', 'NORMAL', NULL, NULL, '2024-01-01 10:00:00'),
(2, 2, 'ORDER', '订单状态更新', '您的订单 #ORD202401010001 已发货', 'UNREAD', 'NORMAL', '/orders/1', '查看订单', '2024-01-01 11:00:00'),
(3, 3, 'STOCK', '库存预警', '产品"生态黑猪肉"库存不足，当前库存：120件', 'UNREAD', 'HIGH', '/products/3', '查看产品', '2024-01-01 12:00:00'),
(4, 1, 'PROMOTION', '促销活动', '新用户专享优惠券已发放，请查收', 'READ', 'NORMAL', '/coupons', '查看优惠券', '2024-01-01 13:00:00'),
(5, 2, 'SYSTEM', '系统维护通知', '系统将于今晚22:00-24:00进行维护', 'UNREAD', 'NORMAL', NULL, NULL, '2024-01-01 14:00:00');

-- 6.8 插入产品评价数据
INSERT INTO reviews (id, product_id, user_id, order_id, rating, comment, likes, status, created_at) VALUES
(1, 1, 4, 1, 5, '土鸡很新鲜，肉质很好，推荐购买！', 3, 1, '2024-01-01 15:00:00'),
(2, 2, 5, 2, 4, '鸡蛋很新鲜，包装也很好，下次还会买', 1, 1, '2024-01-01 16:00:00'),
(3, 3, 4, 3, 5, '猪肉很肥美，炖汤很香', 2, 1, '2024-01-01 17:00:00');

-- 6.9 插入用户地址数据
INSERT INTO user_addresses (id, user_id, name, phone, province, city, district, address, is_default, created_at) VALUES
(1, 4, '王五', '13800138003', '北京市', '北京市', '朝阳区', '某某小区1号楼101室', 1, '2024-01-01 10:00:00'),
(2, 5, '赵六', '13800138004', '上海市', '上海市', '浦东新区', '某某路123号', 1, '2024-01-01 10:00:00'),
(3, 4, '王五', '13800138003', '北京市', '北京市', '海淀区', '某某大厦2号楼201室', 0, '2024-01-01 11:00:00');

-- 6.10 插入用户收藏数据
INSERT INTO user_favorites (id, user_id, type, target_id, created_at) VALUES
(1, 4, 'PRODUCT', 1, '2024-01-01 10:00:00'),
(2, 4, 'PRODUCT', 2, '2024-01-01 10:00:00'),
(3, 5, 'SELLER', 2, '2024-01-01 10:00:00'),
(4, 5, 'PRODUCT', 3, '2024-01-01 10:00:00');

-- 6.11 插入浏览历史数据
INSERT INTO browse_history (id, user_id, product_id, browse_time) VALUES
(1, 4, 1, '2024-01-01 10:00:00'),
(2, 4, 2, '2024-01-01 10:30:00'),
(3, 5, 3, '2024-01-01 11:00:00'),
(4, 5, 4, '2024-01-01 11:30:00');

-- ==============================================
-- 7. 创建索引优化查询性能
-- ==============================================

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_variety ON products(variety);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_notifications_user_status ON notifications(user_id, status);
CREATE INDEX idx_reviews_product_rating ON reviews(product_id, rating);

-- ==============================================
-- 8. 创建视图
-- ==============================================

-- 8.1 产品统计视图
CREATE VIEW v_product_stats AS
SELECT 
    p.id,
    p.name,
    p.variety,
    p.price,
    p.stock_quantity,
    p.status,
    u.real_name as seller_name,
    COUNT(o.id) as order_count,
    COALESCE(SUM(o.quantity), 0) as total_sold,
    COALESCE(AVG(r.rating), 0) as avg_rating,
    COUNT(r.id) as review_count
FROM products p
LEFT JOIN users u ON p.seller_id = u.id
LEFT JOIN orders o ON p.id = o.product_id
LEFT JOIN reviews r ON p.id = r.product_id
GROUP BY p.id, p.name, p.variety, p.price, p.stock_quantity, p.status, u.real_name;

-- 8.2 用户统计视图
CREATE VIEW v_user_stats AS
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.role,
    COUNT(DISTINCT CASE WHEN u.role = 'SELLER' THEN p.id END) as product_count,
    COUNT(DISTINCT CASE WHEN u.role = 'BUYER' THEN o.id END) as order_count,
    COUNT(DISTINCT f.id) as favorite_count,
    COUNT(DISTINCT n.id) as notification_count
FROM users u
LEFT JOIN products p ON u.id = p.seller_id
LEFT JOIN orders o ON u.id = o.buyer_id
LEFT JOIN user_favorites f ON u.id = f.user_id
LEFT JOIN notifications n ON u.id = n.user_id
GROUP BY u.id, u.username, u.real_name, u.role;

-- ==============================================
-- 9. 完成提示
-- ==============================================

SELECT '数据库创建完成！' as message;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as product_count FROM products;
SELECT COUNT(*) as order_count FROM orders;
SELECT COUNT(*) as notification_count FROM notifications;
SELECT COUNT(*) as review_count FROM reviews;
