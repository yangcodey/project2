<template>
  <div class="dashboard">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>侬帮我养</h2>
          <p>在线销售预约系统</p>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">个人中心</el-menu-item>
          <el-menu-item index="products" v-if="userInfo.role === 'SELLER'">产品管理</el-menu-item>
          <el-menu-item index="seller-products" v-if="userInfo.role === 'SELLER'">我的产品</el-menu-item>
          <el-menu-item index="orders">订单管理</el-menu-item>
          <el-menu-item index="profile">个人资料</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <!-- 通知中心 -->
          <NotificationCenter />
          
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.realName?.charAt(0) }}</el-avatar>
              <span class="username">{{ userInfo.realName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <div class="dashboard-container">
        <!-- 欢迎信息 -->
        <div class="welcome-section">
          <h1>欢迎回来，{{ userInfo.realName }}！</h1>
          <p>您的角色：{{ getRoleText(userInfo.role) }}</p>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-section">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#409EFF"><ShoppingCart /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ stats.totalOrders }}</h3>
                    <p>总订单数</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#67C23A"><Box /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ stats.totalProducts }}</h3>
                    <p>产品数量</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#E6A23C"><Money /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>¥{{ stats.totalAmount }}</h3>
                    <p>总金额</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#F56C6C"><User /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ stats.pendingOrders }}</h3>
                    <p>待处理订单</p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 快速操作 -->
        <div class="quick-actions">
          <h2>快速操作</h2>
          <el-row :gutter="20">
            <el-col :span="6" v-if="userInfo.role === 'SELLER'">
              <el-card class="action-card" @click="goToSellerProducts">
                <div class="action-content">
                  <el-icon size="48" color="#409EFF"><Plus /></el-icon>
                  <h3>管理产品</h3>
                  <p>上架、下架、编辑产品</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="action-card" @click="goToOrders">
                <div class="action-content">
                  <el-icon size="48" color="#67C23A"><List /></el-icon>
                  <h3>查看订单</h3>
                  <p>管理您的订单</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="action-card" @click="goToAddressManagement">
                <div class="action-content">
                  <el-icon size="48" color="#E6A23C"><Location /></el-icon>
                  <h3>收货地址</h3>
                  <p>管理收货地址</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="action-card" @click="goToFavorites">
                <div class="action-content">
                  <el-icon size="48" color="#F56C6C"><Star /></el-icon>
                  <h3>我的收藏</h3>
                  <p>查看收藏的商品</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="6">
              <el-card class="action-card" @click="goToBrowseHistory">
                <div class="action-content">
                  <el-icon size="48" color="#909399"><Clock /></el-icon>
                  <h3>浏览历史</h3>
                  <p>查看浏览记录</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="action-card" @click="goToProfile">
                <div class="action-content">
                  <el-icon size="48" color="#E6A23C"><Setting /></el-icon>
                  <h3>个人设置</h3>
                  <p>修改个人信息</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6" v-if="userInfo.role === 'BUYER'">
              <el-card class="action-card" @click="goToProducts">
                <div class="action-content">
                  <el-icon size="48" color="#67C23A"><ShoppingCart /></el-icon>
                  <h3>浏览商品</h3>
                  <p>查看所有商品</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6" v-if="userInfo.role === 'SELLER'">
              <el-card class="action-card" @click="goToMarketplace">
                <div class="action-content">
                  <el-icon size="48" color="#409EFF"><Shop /></el-icon>
                  <h3>商城采购</h3>
                  <p>从其他销售方采购</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 最近订单 -->
        <div class="recent-orders">
          <h2>最近订单</h2>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="productName" label="产品名称" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="totalAmount" label="金额" width="120">
              <template #default="scope">
                ¥{{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="orderTime" label="下单时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="viewOrder(scope.row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Box, Money, User, Plus, List, Setting, Location, Star, Clock, Shop } from '@element-plus/icons-vue'
import { orderApi, productApi } from '../api'
import NotificationCenter from '../components/NotificationCenter.vue'

const router = useRouter()

// 响应式数据
const activeMenu = ref('dashboard')
const stats = ref({
  totalOrders: 0,
  totalProducts: 0,
  totalAmount: 0,
  pendingOrders: 0
})
const recentOrders = ref([])

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const handleMenuSelect = (key) => {
  switch (key) {
    case 'dashboard':
      // 当前页面
      break
    case 'products':
      router.push('/products')
      break
    case 'seller-products':
      router.push('/seller-products')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'profile':
      router.push('/profile')
      break
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}

const getRoleText = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'SELLER': '销售方',
    'BUYER': '购买方'
  }
  return roleMap[role] || '未知'
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING_PAYMENT': '待支付',
    'PAID': '已支付',
    'CANCELLED': '已取消',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING_PAYMENT': 'warning',
    'PAID': 'success',
    'CANCELLED': 'danger',
    'SHIPPED': 'primary',
    'DELIVERED': 'success',
    'COMPLETED': 'success'
  }
  return typeMap[status] || 'info'
}

const goToSellerProducts = () => {
  router.push('/seller-products')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToProfile = () => {
  router.push('/profile')
}

const goToAddressManagement = () => {
  router.push('/address-management')
}

const goToFavorites = () => {
  router.push('/favorites')
}

const goToBrowseHistory = () => {
  router.push('/browse-history')
}

const goToProducts = () => {
  router.push('/products')
}

const goToMarketplace = () => {
  router.push('/marketplace')
}

const viewOrder = (order) => {
  // 可以跳转到订单详情页面
  console.log('查看订单:', order)
}

const loadStats = async () => {
  try {
    // 加载统计数据
    const ordersResponse = await orderApi.getAllOrders()
    if (ordersResponse.data.code === 200) {
      const orders = ordersResponse.data.data
      stats.value.totalOrders = orders.length
      stats.value.totalAmount = orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
      stats.value.pendingOrders = orders.filter(order => order.status === 'PENDING_PAYMENT').length
    }

    const productsResponse = await productApi.getAllProducts()
    if (productsResponse.data.code === 200) {
      stats.value.totalProducts = productsResponse.data.data.length
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRecentOrders = async () => {
  try {
    const response = await orderApi.getAllOrders()
    if (response.data.code === 200) {
      // 只显示最近5个订单
      recentOrders.value = response.data.data.slice(0, 5).map(order => ({
        ...order,
        productName: '产品名称' // 这里应该从产品信息中获取
      }))
    }
  } catch (error) {
    console.error('加载最近订单失败:', error)
  }
}

onMounted(() => {
  loadStats()
  loadRecentOrders()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 0;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.logo h2 {
  color: #409EFF;
  margin: 0;
  font-size: 24px;
}

.logo p {
  color: #666;
  margin: 0;
  font-size: 12px;
}

.nav-menu {
  border-bottom: none;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #333;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.welcome-section {
  margin-bottom: 30px;
  text-align: center;
}

.welcome-section h1 {
  color: #333;
  margin-bottom: 10px;
}

.welcome-section p {
  color: #666;
  font-size: 16px;
}

.stats-section {
  margin-bottom: 40px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  flex-shrink: 0;
}

.stat-info h3 {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 4px 0;
  color: #333;
}

.stat-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.quick-actions {
  margin-bottom: 40px;
}

.quick-actions h2 {
  margin-bottom: 20px;
  color: #333;
}

.action-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  text-align: center;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.action-content {
  padding: 20px;
}

.action-content h3 {
  margin: 16px 0 8px 0;
  color: #333;
}

.action-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.recent-orders h2 {
  margin-bottom: 20px;
  color: #333;
}

/* 移动端响应式 */
@media (max-width: 768px) {
  .nav-container {
    flex-direction: column;
    height: auto;
    padding: 10px 20px;
  }
  
  .nav-menu {
    margin: 10px 0;
  }
  
  .user-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .dashboard-container {
    padding: 15px;
  }
  
  .stats-section .el-col {
    margin-bottom: 15px;
  }
  
  .quick-actions .el-col {
    margin-bottom: 15px;
  }
  
  .action-card {
    height: 120px;
  }
  
  .action-content h3 {
    font-size: 16px;
  }
  
  .action-content p {
    font-size: 12px;
  }
  
  .recent-orders {
    overflow-x: auto;
  }
  
  .recent-orders .el-table {
    min-width: 600px;
  }
}

@media (max-width: 480px) {
  .dashboard-container {
    padding: 10px;
  }
  
  .welcome-section h1 {
    font-size: 24px;
  }
  
  .stats-section .el-col {
    margin-bottom: 10px;
  }
  
  .quick-actions .el-col {
    margin-bottom: 10px;
  }
  
  .action-card {
    height: 100px;
  }
  
  .action-content .el-icon {
    font-size: 32px;
  }
  
  .action-content h3 {
    font-size: 14px;
    margin: 8px 0 4px 0;
  }
  
  .action-content p {
    font-size: 11px;
    margin: 0;
  }
}
</style>
