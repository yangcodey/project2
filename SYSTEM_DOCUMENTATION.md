# 侬帮我养农产品销售系统 - 完整技术文档

## 系统概述

这是一个基于Spring Boot + Vue.js的现代化农产品销售平台，支持多角色用户（管理员、销售方、购买方）的完整电商功能，包括产品管理、订单处理、评价系统、通知系统等核心功能。

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.5.5
- **Java版本**: 17
- **数据库**: MySQL 8.0.33
- **ORM**: MyBatis 3.0.5
- **认证**: JWT (JSON Web Token)
- **缓存**: Redis (Spring Data Redis)
- **构建工具**: Maven
- **其他依赖**:
  - Lombok (代码简化)
  - Jackson (JSON处理)
  - Apache Commons Lang3 (工具类)
  - Spring Boot Validation (数据验证)

### 前端技术栈
- **框架**: Vue.js 3.5.18
- **构建工具**: Vite 7.1.2
- **UI组件库**: Element Plus 2.11.2
- **图标**: Element Plus Icons Vue 2.3.1
- **路由**: Vue Router 4.4.5
- **HTTP客户端**: Axios 1.7.7
- **图表**: ECharts 5.5.1 + Vue-ECharts 6.6.1
- **包管理**: pnpm

## 系统环境要求

### 开发环境
- **JDK**: 17或更高版本
- **Node.js**: 16或更高版本
- **MySQL**: 8.0或更高版本
- **Redis**: 6.0或更高版本（可选）
- **Maven**: 3.6或更高版本
- **IDE**: IntelliJ IDEA / Eclipse / VS Code

### 生产环境
- **服务器**: Linux/Windows Server
- **内存**: 最少2GB，推荐4GB以上
- **存储**: 最少10GB可用空间
- **网络**: 稳定的互联网连接

## 项目结构

### 后端结构
```
src/main/java/com/example/project2/
├── Project2Application.java          # Spring Boot启动类
├── common/
│   └── Result.java                   # 统一返回结果封装
├── config/
│   ├── CorsConfig.java              # 跨域配置
│   └── SecurityConfig.java          # 安全配置
├── controller/                       # 控制器层
│   ├── UserController.java          # 用户管理
│   ├── ProductController.java       # 产品管理
│   ├── OrderController.java         # 订单管理
│   ├── ReviewController.java        # 评价管理
│   ├── NotificationController.java  # 通知管理
│   ├── StatisticsController.java    # 统计分析
│   ├── MarketplaceController.java   # 市场功能
│   └── ...                         # 其他控制器
├── entity/                          # 实体类
│   ├── User.java                    # 用户实体
│   ├── Product.java                 # 产品实体
│   ├── Order.java                   # 订单实体
│   ├── Review.java                  # 评价实体
│   ├── Notification.java            # 通知实体
│   └── ...                         # 其他实体
├── mapper/                          # 数据访问层
│   ├── UserMapper.java              # 用户数据访问
│   ├── ProductMapper.java           # 产品数据访问
│   ├── OrderMapper.java             # 订单数据访问
│   └── ...                         # 其他Mapper
├── service/                         # 业务逻辑层
│   ├── UserService.java             # 用户服务
│   ├── ProductService.java          # 产品服务
│   ├── OrderService.java            # 订单服务
│   └── ...                         # 其他服务
├── typehandler/
│   └── JsonTypeHandler.java         # JSON类型处理器
└── util/
    └── JwtUtil.java                 # JWT工具类
```

### 前端结构
```
vite-project/src/
├── main.js                          # 应用入口
├── App.vue                          # 根组件
├── api/
│   └── index.js                     # API接口定义
├── components/                      # 公共组件
│   ├── MobileBottomNav.vue          # 移动端底部导航
│   └── NotificationCenter.vue       # 通知中心
├── views/                           # 页面组件
│   ├── Login.vue                    # 登录页面
│   ├── Register.vue                 # 注册页面
│   ├── Home.vue                     # 首页
│   ├── Products.vue                 # 产品列表
│   ├── ProductDetail.vue            # 产品详情
│   ├── Admin.vue                    # 管理员页面
│   ├── SellerDashboard.vue          # 销售方仪表板
│   ├── SellerProducts.vue           # 销售方产品管理
│   ├── BuyerDashboard.vue           # 购买方仪表板
│   ├── Orders.vue                   # 订单管理
│   ├── ProductReviews.vue           # 产品评价
│   ├── AddressManagement.vue        # 地址管理
│   ├── Favorites.vue                # 收藏夹
│   ├── BrowseHistory.vue            # 浏览历史
│   ├── AdvancedSearch.vue           # 高级搜索
│   ├── Analytics.vue                # 数据分析
│   └── ...                         # 其他页面
├── router/
│   └── index.js                     # 路由配置
└── style.css                        # 全局样式
```

## 核心功能模块

### 1. 用户管理系统
- **角色定义**:
  - `ADMIN`: 管理员 - 系统管理、用户管理、产品审核
  - `SELLER`: 销售方 - 产品管理、订单处理、销售统计
  - `BUYER`: 购买方 - 产品浏览、下单购买、评价反馈

- **功能特性**:
  - 用户注册/登录
  - JWT身份认证
  - 角色权限控制
  - 用户信息管理

### 2. 产品管理系统
- **产品信息**:
  - 基本信息：名称、品种、规格、价格
  - 详细信息：描述、图片、养殖周期
  - 库存管理：库存数量、预警阈值
  - 状态管理：待审核、已上架、已下架、缺货

- **核心功能**:
  - 产品CRUD操作
  - 产品审核流程
  - 库存预警
  - 产品搜索和筛选
  - 产品统计分析

### 3. 订单管理系统
- **订单状态**:
  - `PENDING`: 待支付
  - `PAID`: 已支付
  - `SHIPPED`: 已发货
  - `DELIVERED`: 已送达
  - `CANCELLED`: 已取消
  - `REFUNDED`: 已退款

- **功能特性**:
  - 订单创建和支付
  - 订单状态跟踪
  - 订单历史查询
  - 订单统计分析

### 4. 评价系统
- **评价维度**:
  - 产品质量评分（1-5星）
  - 服务态度评分
  - 物流速度评分
  - 文字评价内容

- **功能特性**:
  - 评价发布和管理
  - 评价统计和展示
  - 评价回复功能

### 5. 通知系统
- **通知类型**:
  - `ORDER`: 订单相关通知
  - `SYSTEM`: 系统通知
  - `PROMOTION`: 促销活动通知
  - `STOCK`: 库存预警通知

- **功能特性**:
  - 实时通知推送
  - 通知历史查询
  - 通知状态管理

### 6. 个人中心功能
- **地址管理**: 收货地址的增删改查
- **收藏夹**: 产品收藏功能
- **浏览历史**: 产品浏览记录
- **个人资料**: 用户信息管理

### 7. 数据分析系统
- **销售统计**: 销量、收入、趋势分析
- **产品分析**: 热销产品、库存分析
- **用户分析**: 用户行为、活跃度统计
- **图表展示**: ECharts数据可视化

## 数据库设计

### 核心表结构

#### 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'SELLER', 'BUYER') NOT NULL,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 产品表 (products)
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    variety VARCHAR(100) NOT NULL,
    specification VARCHAR(100),
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    breeding_cycle INT,
    status TINYINT DEFAULT 0,
    seller_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id)
);
```

#### 订单表 (orders)
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    buyer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    shipping_address JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
);
```

### 扩展表结构
- **评价表 (reviews)**: 产品评价和评分
- **通知表 (notifications)**: 系统通知管理
- **地址表 (addresses)**: 用户收货地址
- **收藏表 (favorites)**: 用户收藏产品
- **浏览历史表 (browse_history)**: 用户浏览记录

## API接口设计

### 认证接口
```
POST /api/auth/login          # 用户登录
POST /api/auth/register       # 用户注册
POST /api/auth/logout         # 用户登出
GET  /api/auth/profile        # 获取用户信息
```

### 产品接口
```
GET    /api/products          # 获取产品列表
GET    /api/products/{id}     # 获取产品详情
POST   /api/products          # 创建产品
PUT    /api/products/{id}     # 更新产品
DELETE /api/products/{id}     # 删除产品
GET    /api/products/search   # 搜索产品
```

### 订单接口
```
GET    /api/orders            # 获取订单列表
GET    /api/orders/{id}       # 获取订单详情
POST   /api/orders            # 创建订单
PUT    /api/orders/{id}       # 更新订单状态
DELETE /api/orders/{id}       # 取消订单
```

### 评价接口
```
GET    /api/reviews           # 获取评价列表
POST   /api/reviews           # 创建评价
PUT    /api/reviews/{id}      # 更新评价
DELETE /api/reviews/{id}      # 删除评价
```

## 系统部署

### 1. 环境准备
```bash
# 安装Java 17
# 安装Node.js 16+
# 安装MySQL 8.0+
# 安装Redis (可选)
```

### 2. 数据库初始化
```bash
# 执行数据库脚本
mysql -u root -p < NAVICAT_STABLE_DATABASE.sql
```

### 3. 后端部署
```bash
# 进入项目根目录
cd D:\hallo\project2

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/project2-0.0.1-SNAPSHOT.jar
```

### 4. 前端部署
```bash
# 进入前端目录
cd vite-project

# 安装依赖
pnpm install

# 开发模式运行
pnpm dev

# 生产构建
pnpm build
```

### 5. 配置文件
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/nongbangwoyang?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.example.project2.entity

server.port=8080
```

## 系统调用关系

### 前端调用流程
```
用户操作 → Vue组件 → API调用 → Axios请求 → 后端Controller → Service层 → Mapper层 → 数据库
```

### 后端架构层次
```
Controller层 (控制器)
    ↓
Service层 (业务逻辑)
    ↓
Mapper层 (数据访问)
    ↓
Database (数据库)
```

### 关键调用链
1. **用户登录**: `Login.vue` → `UserController.login()` → `UserService.authenticate()` → `UserMapper.findByUsername()`
2. **产品浏览**: `Products.vue` → `ProductController.getProducts()` → `ProductService.getProducts()` → `ProductMapper.selectAll()`
3. **订单创建**: `ProductDetail.vue` → `OrderController.createOrder()` → `OrderService.createOrder()` → `OrderMapper.insert()`
4. **评价发布**: `ProductReviews.vue` → `ReviewController.createReview()` → `ReviewService.createReview()` → `ReviewMapper.insert()`

## 开发指南

### 1. 添加新功能
1. 在`entity`包中创建实体类
2. 在`mapper`包中创建Mapper接口和XML
3. 在`service`包中创建Service类
4. 在`controller`包中创建Controller类
5. 在前端`views`中创建Vue组件
6. 在`router/index.js`中添加路由

### 2. 数据库操作
- 使用MyBatis进行数据库操作
- 在`mapper`包中定义接口方法
- 在`resources/mapper`中编写SQL语句
- 使用`@Mapper`注解标记接口

### 3. 前端开发
- 使用Vue 3 Composition API
- 遵循Element Plus设计规范
- 使用Axios进行API调用
- 响应式设计支持移动端

### 4. 调试技巧
- 后端：使用`@Slf4j`注解记录日志
- 前端：使用浏览器开发者工具
- 数据库：使用Navicat等工具查看数据
- API：使用Postman测试接口

## 常见问题解决

### 1. 数据库连接问题
- 检查MySQL服务是否启动
- 验证数据库连接配置
- 确认数据库用户权限

### 2. 跨域问题
- 检查`CorsConfig`配置
- 验证前端请求URL
- 确认后端端口配置

### 3. 前端构建问题
- 检查Node.js版本
- 清除node_modules重新安装
- 检查Vite配置

### 4. 权限问题
- 验证JWT token有效性
- 检查用户角色权限
- 确认API路径配置

## 系统特色功能

### 1. 多角色支持
- 管理员：系统管理、用户管理、产品审核
- 销售方：产品管理、订单处理、销售统计
- 购买方：产品浏览、下单购买、评价反馈

### 2. 产品审核机制
- 销售方提交产品后需要管理员审核
- 审核通过后才能上架销售
- 支持审核历史和状态跟踪

### 3. 实时通知系统
- 订单状态变更通知
- 库存预警通知
- 系统公告通知
- 支持通知中心统一管理

### 4. 移动端适配
- 响应式设计
- 移动端底部导航
- 触摸友好的交互设计

### 5. 数据分析功能
- 销售数据统计
- 产品热度分析
- 用户行为分析
- 图表可视化展示

## 技术亮点

1. **前后端分离**: 使用Vue.js + Spring Boot实现完全分离的架构
2. **RESTful API**: 遵循REST设计原则的API接口
3. **JWT认证**: 无状态的用户认证机制
4. **响应式设计**: 支持PC端和移动端的多设备适配
5. **组件化开发**: 前端组件化，后端分层架构
6. **数据可视化**: 集成ECharts实现数据图表展示
7. **现代化UI**: 使用Element Plus提供美观的用户界面

## 扩展建议

### 1. 功能扩展
- 支付系统集成（支付宝、微信支付）
- 物流跟踪系统
- 优惠券和促销活动
- 商品推荐算法
- 客服聊天系统

### 2. 技术优化
- 引入Redis缓存提升性能
- 使用Elasticsearch实现全文搜索
- 集成消息队列处理异步任务
- 添加API限流和熔断机制
- 实现微服务架构

### 3. 部署优化
- 使用Docker容器化部署
- 配置Nginx反向代理
- 实现CI/CD自动化部署
- 添加监控和日志系统

---

**系统版本**: 1.0.0  
**最后更新**: 2024年1月  
**维护团队**: 侬帮我养开发团队
