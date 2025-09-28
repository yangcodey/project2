<template>
  <div class="seller-dashboard">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>销售方工作台</h2>
          <p>{{ userInfo.realName }} - 欢迎回来</p>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="overview">概览</el-menu-item>
          <el-menu-item index="products">产品管理</el-menu-item>
          <el-menu-item index="orders">订单管理</el-menu-item>
          <el-menu-item index="customers">客户管理</el-menu-item>
          <el-menu-item index="analytics">数据分析</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.realName?.charAt(0) }}</el-avatar>
              <span class="username">{{ userInfo.realName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">店铺设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 概览页面 -->
      <div v-if="activeMenu === 'overview'" class="overview-section">
        <!-- 关键指标卡片 -->
        <el-row :gutter="20" class="metrics-cards">
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon size="32" color="#409EFF"><Box /></el-icon>
                </div>
                <div class="metric-info">
                  <h3>{{ metrics.totalProducts }}</h3>
                  <p>总产品数</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon size="32" color="#67C23A"><TrendCharts /></el-icon>
                </div>
                <div class="metric-info">
                  <h3>{{ metrics.activeProducts }}</h3>
                  <p>上架产品</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon size="32" color="#E6A23C"><ShoppingCart /></el-icon>
                </div>
                <div class="metric-info">
                  <h3>{{ metrics.totalOrders }}</h3>
                  <p>总订单数</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon size="32" color="#F56C6C"><Money /></el-icon>
                </div>
                <div class="metric-info">
                  <h3>¥{{ metrics.totalRevenue }}</h3>
                  <p>总收入</p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 快速操作 -->
        <el-row :gutter="20" class="quick-actions">
          <el-col :span="12">
            <el-card class="action-card">
              <h3>快速操作</h3>
              <div class="action-buttons">
                <el-button type="primary" @click="goToProducts">
                  <el-icon><Plus /></el-icon>
                  添加产品
                </el-button>
                <el-button type="success" @click="goToOrders">
                  <el-icon><List /></el-icon>
                  查看订单
                </el-button>
                <el-button type="info" @click="goToMarketplace">
                  <el-icon><Shop /></el-icon>
                  采购商城
                </el-button>
                <el-button type="warning" @click="goToAnalytics">
                  <el-icon><TrendCharts /></el-icon>
                  数据分析
                </el-button>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="action-card">
              <h3>库存预警</h3>
              <div v-if="lowStockProducts.length === 0" class="no-warning">
                <el-icon size="48" color="#67C23A"><CircleCheck /></el-icon>
                <p>所有产品库存充足</p>
              </div>
              <div v-else class="warning-list">
                <div v-for="product in lowStockProducts" :key="product.id" class="warning-item">
                  <span>{{ product.name }}</span>
                  <el-tag type="warning">库存: {{ product.stockQuantity }}</el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 最近订单 -->
        <el-card class="recent-orders">
          <template #header>
            <div class="card-header">
              <h3>最近订单</h3>
              <el-button type="text" @click="goToOrders">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="productName" label="产品名称" />
            <el-table-column prop="buyerName" label="购买方" width="120" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.status)">
                  {{ getOrderStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="orderTime" label="下单时间" width="150" />
          </el-table>
        </el-card>
      </div>

      <!-- 产品管理页面 -->
      <div v-if="activeMenu === 'products'" class="products-section">
        <div class="section-header">
          <h2>产品管理</h2>
          <el-button type="primary" @click="goToSellerProducts">管理产品</el-button>
        </div>
        <p>产品管理功能已集成到专门的产品管理页面，请点击上方按钮前往管理。</p>
      </div>

      <!-- 订单管理页面 -->
      <div v-if="activeMenu === 'orders'" class="orders-section">
        <div class="section-header">
          <h2>订单管理</h2>
          <el-button type="primary" @click="goToOrders">管理订单</el-button>
        </div>
        <p>订单管理功能已集成到专门的订单管理页面，请点击上方按钮前往管理。</p>
      </div>

      <!-- 客户管理页面 -->
      <div v-if="activeMenu === 'customers'" class="customers-section">
        <div class="section-header">
          <h2>客户管理</h2>
          <el-button type="primary" @click="loadCustomers">刷新客户列表</el-button>
        </div>
        
        <el-table :data="customers" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="realName" label="客户姓名" width="120" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="totalOrders" label="订单数" width="100" />
          <el-table-column prop="totalAmount" label="消费金额" width="120">
            <template #default="{ row }">
              ¥{{ row.totalAmount }}
            </template>
          </el-table-column>
          <el-table-column prop="lastOrderTime" label="最后下单时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="viewCustomer(row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 数据分析页面 -->
      <div v-if="activeMenu === 'analytics'" class="analytics-section">
        <div class="section-header">
          <h2>数据分析</h2>
          <el-button type="primary" @click="goToAnalytics">详细分析</el-button>
        </div>
        <p>数据分析功能已集成到专门的数据分析页面，请点击上方按钮前往查看。</p>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Box, TrendCharts, ShoppingCart, Money, Plus, List, CircleCheck } from '@element-plus/icons-vue'
import { productApi, orderApi } from '../api'

const router = useRouter()

// 响应式数据
const activeMenu = ref('overview')
const loading = ref(false)
const metrics = ref({
  totalProducts: 0,
  activeProducts: 0,
  totalOrders: 0,
  totalRevenue: 0
})
const lowStockProducts = ref([])
const recentOrders = ref([])
const customers = ref([])

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const handleMenuSelect = (key) => {
  activeMenu.value = key
  if (key === 'customers') {
    loadCustomers()
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      ElMessage.info('店铺设置功能开发中')
      break
    case 'logout':
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}

const loadMetrics = async () => {
  try {
    // 加载产品数据
    const productsResponse = await productApi.getAllProducts()
    if (productsResponse.data.code === 200) {
      const products = productsResponse.data.data
      metrics.value.totalProducts = products.length
      metrics.value.activeProducts = products.filter(p => p.status === 1).length
      
      // 计算低库存产品
      lowStockProducts.value = products.filter(p => p.stockQuantity <= 5 && p.stockQuantity > 0)
    }

    // 加载订单数据
    const ordersResponse = await orderApi.getAllOrders()
    if (ordersResponse.data.code === 200) {
      const orders = ordersResponse.data.data
      metrics.value.totalOrders = orders.length
      metrics.value.totalRevenue = orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
      
      // 最近订单
      recentOrders.value = orders.slice(0, 5).map(order => ({
        ...order,
        productName: '产品名称',
        buyerName: '购买方名称'
      }))
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    // 使用模拟数据
    loadMockData()
  }
}

const loadMockData = () => {
  metrics.value = {
    totalProducts: 15,
    activeProducts: 12,
    totalOrders: 28,
    totalRevenue: 15680
  }
  
  lowStockProducts.value = [
    { id: 1, name: '优质土鸡', stockQuantity: 3 },
    { id: 2, name: '生态鸭', stockQuantity: 2 }
  ]
  
  recentOrders.value = [
    {
      orderNo: 'NBWY20240116001',
      productName: '优质土鸡',
      buyerName: '王五',
      quantity: 2,
      totalAmount: 136.00,
      status: 'PAID',
      orderTime: '2024-01-16 10:30:00'
    },
    {
      orderNo: 'NBWY20240116002',
      productName: '生态鸭',
      buyerName: '赵六',
      quantity: 1,
      totalAmount: 45.00,
      status: 'SHIPPED',
      orderTime: '2024-01-16 11:00:00'
    }
  ]
}

const loadCustomers = async () => {
  try {
    loading.value = true
    // 这里应该调用获取客户列表的API
    // 模拟客户数据
    customers.value = [
      {
        id: 1,
        realName: '王五',
        phone: '13800138003',
        email: 'buyer1@example.com',
        totalOrders: 5,
        totalAmount: 680.00,
        lastOrderTime: '2024-01-16 10:30:00'
      },
      {
        id: 2,
        realName: '赵六',
        phone: '13800138004',
        email: 'buyer2@example.com',
        totalOrders: 3,
        totalAmount: 135.00,
        lastOrderTime: '2024-01-16 11:00:00'
      }
    ]
  } catch (error) {
    console.error('加载客户数据失败:', error)
    ElMessage.error('加载客户数据失败')
  } finally {
    loading.value = false
  }
}

const goToProducts = () => {
  router.push('/seller-products')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToAnalytics = () => {
  router.push('/analytics')
}

const goToMarketplace = () => {
  router.push('/marketplace')
}

const goToSellerProducts = () => {
  router.push('/seller-products')
}

const viewCustomer = (customer) => {
  ElMessage.info(`查看客户 ${customer.realName} 的详细信息`)
}

const getOrderStatusText = (status) => {
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

const getOrderStatusType = (status) => {
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

onMounted(() => {
  loadMetrics()
})
</script>

<style scoped>
.seller-dashboard {
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
  font-size: 20px;
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

.metrics-cards {
  margin-bottom: 30px;
}

.metric-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.metric-card:hover {
  transform: translateY(-2px);
}

.metric-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.metric-icon {
  flex-shrink: 0;
}

.metric-info h3 {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 4px 0;
  color: #333;
}

.metric-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.quick-actions {
  margin-bottom: 30px;
}

.action-card {
  height: 200px;
}

.action-card h3 {
  margin: 0 0 20px 0;
  color: #333;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.no-warning {
  text-align: center;
  padding: 20px;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.warning-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.warning-item:last-child {
  border-bottom: none;
}

.recent-orders {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #333;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  color: #333;
}
</style>
