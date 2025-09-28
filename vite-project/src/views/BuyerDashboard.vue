<template>
  <div class="buyer-dashboard">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>购买方工作台</h2>
          <p>{{ userInfo.realName }} - 欢迎回来</p>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="overview">概览</el-menu-item>
          <el-menu-item index="products">产品浏览</el-menu-item>
          <el-menu-item index="orders">我的订单</el-menu-item>
          <el-menu-item index="favorites">收藏夹</el-menu-item>
          <el-menu-item index="sellers">关注商家</el-menu-item>
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
                <el-dropdown-item command="address">收货地址</el-dropdown-item>
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
        <!-- 个人统计卡片 -->
        <el-row :gutter="20" class="metrics-cards">
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon size="32" color="#409EFF"><ShoppingCart /></el-icon>
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
                  <el-icon size="32" color="#67C23A"><CircleCheck /></el-icon>
                </div>
                <div class="metric-info">
                  <h3>{{ metrics.completedOrders }}</h3>
                  <p>已完成订单</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon size="32" color="#E6A23C"><Money /></el-icon>
                </div>
                <div class="metric-info">
                  <h3>¥{{ metrics.totalSpent }}</h3>
                  <p>总消费金额</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon size="32" color="#F56C6C"><Star /></el-icon>
                </div>
                <div class="metric-info">
                  <h3>{{ metrics.favoriteProducts }}</h3>
                  <p>收藏产品</p>
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
                  <el-icon><Box /></el-icon>
                  浏览产品
                </el-button>
                <el-button type="success" @click="goToOrders">
                  <el-icon><List /></el-icon>
                  查看订单
                </el-button>
                <el-button type="warning" @click="goToFavorites">
                  <el-icon><Star /></el-icon>
                  我的收藏
                </el-button>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="action-card">
              <h3>推荐产品</h3>
              <div v-if="recommendedProducts.length === 0" class="no-recommendations">
                <el-icon size="48" color="#909399"><Box /></el-icon>
                <p>暂无推荐产品</p>
              </div>
              <div v-else class="recommendation-list">
                <div v-for="product in recommendedProducts" :key="product.id" class="recommendation-item">
                  <img :src="product.imageUrl" :alt="product.name" class="product-image" />
                  <div class="product-info">
                    <h4>{{ product.name }}</h4>
                    <p class="price">¥{{ product.price }}</p>
                  </div>
                  <el-button size="small" type="primary" @click="viewProduct(product)">查看</el-button>
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
            <el-table-column prop="sellerName" label="销售方" width="120" />
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
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" @click="viewOrder(row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <!-- 产品浏览页面 -->
      <div v-if="activeMenu === 'products'" class="products-section">
        <div class="section-header">
          <h2>产品浏览</h2>
          <el-button type="primary" @click="goToProducts">浏览所有产品</el-button>
        </div>
        <p>产品浏览功能已集成到专门的产品展示页面，请点击上方按钮前往浏览。</p>
      </div>

      <!-- 订单管理页面 -->
      <div v-if="activeMenu === 'orders'" class="orders-section">
        <div class="section-header">
          <h2>我的订单</h2>
          <el-button type="primary" @click="goToOrders">管理订单</el-button>
        </div>
        <p>订单管理功能已集成到专门的订单管理页面，请点击上方按钮前往管理。</p>
      </div>

      <!-- 收藏夹页面 -->
      <div v-if="activeMenu === 'favorites'" class="favorites-section">
        <div class="section-header">
          <h2>我的收藏</h2>
          <el-button type="primary" @click="loadFavorites">刷新收藏</el-button>
        </div>
        
        <el-table :data="favoriteProducts" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="产品名称" />
          <el-table-column prop="variety" label="品种" width="100" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="stockQuantity" label="库存" width="80" />
          <el-table-column prop="sellerName" label="销售方" width="120" />
          <el-table-column prop="favoriteTime" label="收藏时间" width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="viewProduct(row)">查看</el-button>
              <el-button size="small" type="danger" @click="removeFavorite(row)">取消收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 关注商家页面 -->
      <div v-if="activeMenu === 'sellers'" class="sellers-section">
        <div class="section-header">
          <h2>关注商家</h2>
          <el-button type="primary" @click="loadSellers">刷新列表</el-button>
        </div>
        
        <el-table :data="followedSellers" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="realName" label="商家名称" width="150" />
          <el-table-column prop="phone" label="联系方式" width="120" />
          <el-table-column prop="totalProducts" label="产品数量" width="100" />
          <el-table-column prop="followTime" label="关注时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="viewSeller(row)">查看店铺</el-button>
              <el-button size="small" type="danger" @click="unfollowSeller(row)">取消关注</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, CircleCheck, Money, Star, Box, List } from '@element-plus/icons-vue'
import { productApi, orderApi } from '../api'

const router = useRouter()

// 响应式数据
const activeMenu = ref('overview')
const loading = ref(false)
const metrics = ref({
  totalOrders: 0,
  completedOrders: 0,
  totalSpent: 0,
  favoriteProducts: 0
})
const recommendedProducts = ref([])
const recentOrders = ref([])
const favoriteProducts = ref([])
const followedSellers = ref([])

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const handleMenuSelect = (key) => {
  activeMenu.value = key
  if (key === 'favorites') {
    loadFavorites()
  } else if (key === 'sellers') {
    loadSellers()
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'address':
      ElMessage.info('收货地址管理功能开发中')
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
    // 加载订单数据
    const ordersResponse = await orderApi.getAllOrders()
    if (ordersResponse.data.code === 200) {
      const orders = ordersResponse.data.data
      metrics.value.totalOrders = orders.length
      metrics.value.completedOrders = orders.filter(o => o.status === 'COMPLETED').length
      metrics.value.totalSpent = orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
      
      // 最近订单
      recentOrders.value = orders.slice(0, 5).map(order => ({
        ...order,
        productName: '产品名称',
        sellerName: '销售方名称'
      }))
    }

    // 加载产品数据
    const productsResponse = await productApi.getAllProducts()
    if (productsResponse.data.code === 200) {
      const products = productsResponse.data.data
      metrics.value.favoriteProducts = products.filter(p => p.isFavorite).length
      
      // 推荐产品
      recommendedProducts.value = products.slice(0, 3).map(product => ({
        ...product,
        sellerName: '销售方名称'
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
    totalOrders: 8,
    completedOrders: 6,
    totalSpent: 1240.00,
    favoriteProducts: 5
  }
  
  recommendedProducts.value = [
    {
      id: 1,
      name: '优质土鸡',
      price: 68.00,
      imageUrl: 'https://via.placeholder.com/60x60',
      sellerName: '张三农场'
    },
    {
      id: 2,
      name: '生态鸭',
      price: 45.00,
      imageUrl: 'https://via.placeholder.com/60x60',
      sellerName: '李四养殖场'
    }
  ]
  
  recentOrders.value = [
    {
      orderNo: 'NBWY20240116001',
      productName: '优质土鸡',
      sellerName: '张三农场',
      quantity: 2,
      totalAmount: 136.00,
      status: 'COMPLETED',
      orderTime: '2024-01-16 10:30:00'
    },
    {
      orderNo: 'NBWY20240116002',
      productName: '生态鸭',
      sellerName: '李四养殖场',
      quantity: 1,
      totalAmount: 45.00,
      status: 'SHIPPED',
      orderTime: '2024-01-16 11:00:00'
    }
  ]
}

const loadFavorites = async () => {
  try {
    loading.value = true
    // 模拟收藏数据
    favoriteProducts.value = [
      {
        id: 1,
        name: '优质土鸡',
        variety: '鸡',
        price: 68.00,
        stockQuantity: 10,
        sellerName: '张三农场',
        favoriteTime: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        name: '生态鸭',
        variety: '鸭',
        price: 45.00,
        stockQuantity: 5,
        sellerName: '李四养殖场',
        favoriteTime: '2024-01-14 15:20:00'
      }
    ]
  } catch (error) {
    console.error('加载收藏数据失败:', error)
    ElMessage.error('加载收藏数据失败')
  } finally {
    loading.value = false
  }
}

const loadSellers = async () => {
  try {
    loading.value = true
    // 模拟关注商家数据
    followedSellers.value = [
      {
        id: 1,
        realName: '张三农场',
        phone: '13800138001',
        totalProducts: 15,
        followTime: '2024-01-10 10:00:00'
      },
      {
        id: 2,
        realName: '李四养殖场',
        phone: '13800138002',
        totalProducts: 8,
        followTime: '2024-01-12 14:30:00'
      }
    ]
  } catch (error) {
    console.error('加载商家数据失败:', error)
    ElMessage.error('加载商家数据失败')
  } finally {
    loading.value = false
  }
}

const goToProducts = () => {
  router.push('/products')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToFavorites = () => {
  activeMenu.value = 'favorites'
  loadFavorites()
}

const viewProduct = (product) => {
  ElMessage.info(`查看产品 ${product.name}`)
}

const viewOrder = (order) => {
  ElMessage.info(`查看订单 ${order.orderNo}`)
}

const removeFavorite = (product) => {
  ElMessage.success(`已取消收藏 ${product.name}`)
  loadFavorites()
}

const viewSeller = (seller) => {
  ElMessage.info(`查看商家 ${seller.realName} 的店铺`)
}

const unfollowSeller = (seller) => {
  ElMessage.success(`已取消关注 ${seller.realName}`)
  loadSellers()
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
.buyer-dashboard {
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

.no-recommendations {
  text-align: center;
  padding: 20px;
}

.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.recommendation-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.recommendation-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.product-info .price {
  margin: 0;
  font-size: 12px;
  color: #E6A23C;
  font-weight: bold;
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
