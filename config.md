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