<template>
  <div class="home">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>侬帮我养</h2>
          <p>在线销售预约系统</p>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeIndex"
          class="nav-menu"
          @select="handleSelect"
        >
          <el-menu-item index="home">首页</el-menu-item>
          <el-menu-item index="products">产品展示</el-menu-item>
          <el-menu-item index="feature-test">功能测试</el-menu-item>
          <el-menu-item index="about">关于我们</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <el-button v-if="!isLoggedIn" @click="goToLogin" type="primary">登录</el-button>
          <el-button v-if="!isLoggedIn" @click="goToRegister" type="success">注册</el-button>
              <el-dropdown v-if="isLoggedIn" @command="handleUserCommand">
                <span class="user-info">
                  <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.realName?.charAt(0) }}</el-avatar>
                  <span class="username">{{ userInfo.realName }}</span>
                  <el-tag :type="getRoleType(userInfo.role)" size="small">{{ getRoleText(userInfo.role) }}</el-tag>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="dashboard">工作台</el-dropdown-item>
                    <el-dropdown-item command="orders">我的订单</el-dropdown-item>
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
      <!-- 轮播图 -->
      <el-carousel height="400px" class="banner">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
            <div class="banner-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
              <el-button type="primary" size="large" @click="goToProducts">立即预约</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <!-- 产品展示 -->
      <div class="products-section">
        <div class="section-title">
          <h2>热门产品</h2>
          <p>精选优质养殖产品，新鲜直达</p>
        </div>
        <div class="products-grid">
          <el-card v-for="product in featuredProducts" :key="product.id" class="product-card" shadow="hover">
            <img :src="product.imageUrl || '/default-product.jpg'" :alt="product.name" class="product-image">
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="product-variety">{{ product.variety }}</p>
              <p class="product-price">¥{{ product.price }}</p>
              <p class="product-stock">库存：{{ product.stockQuantity }}</p>
              <el-button type="primary" @click="viewProduct(product.id)">查看详情</el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 系统特色 -->
      <div class="features-section">
        <div class="section-title">
          <h2>系统特色</h2>
          <p>为养殖产品销售提供全方位服务</p>
        </div>
        <div class="features-grid">
          <div class="feature-item">
            <el-icon size="48" color="#409EFF"><ShoppingCart /></el-icon>
            <h3>在线预约</h3>
            <p>提前预约心仪产品，确保供应稳定</p>
          </div>
          <div class="feature-item">
            <el-icon size="48" color="#67C23A"><Position /></el-icon>
            <h3>物流跟踪</h3>
            <p>实时跟踪订单状态，掌握配送进度</p>
          </div>
          <div class="feature-item">
            <el-icon size="48" color="#E6A23C"><Menu /></el-icon>
            <h3>数据分析</h3>
            <p>智能分析销售数据，优化养殖计划</p>
          </div>
          <div class="feature-item">
            <el-icon size="48" color="#F56C6C"><User /></el-icon>
            <h3>用户管理</h3>
            <p>完善的用户权限管理，保障交易安全</p>
          </div>
        </div>
      </div>
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 侬帮我养在线销售预约系统. All rights reserved.</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, Position, Menu, User } from '@element-plus/icons-vue'
import { productApi } from '../api'

const router = useRouter()

// 响应式数据
const activeIndex = ref('home')
const featuredProducts = ref([])
const banners = ref([
  {
    id: 1,
    title: '新鲜养殖产品',
    description: '绿色健康，品质保证',
    image: '/banner1.jpg'
  },
  {
    id: 2,
    title: '在线预约服务',
    description: '提前预约，确保供应',
    image: '/banner2.jpg'
  },
  {
    id: 3,
    title: '专业物流配送',
    description: '快速安全，新鲜送达',
    image: '/banner3.jpg'
  }
])

// 计算属性
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') && localStorage.getItem('user')
})

const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const handleSelect = (key) => {
  switch (key) {
    case 'home':
      router.push('/')
      break
    case 'products':
      router.push('/products')
      break
    case 'feature-test':
      router.push('/feature-test')
      break
    case 'about':
      // 可以添加关于我们页面
      break
  }
}

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const goToProducts = () => {
  router.push('/products')
}

const viewProduct = (productId) => {
  router.push(`/products/${productId}`)
}

    const handleUserCommand = (command) => {
      switch (command) {
        case 'dashboard':
          // 根据用户角色跳转到对应的工作台
          const user = JSON.parse(localStorage.getItem('user'))
          switch (user.role) {
            case 'ADMIN':
              router.push('/admin')
              break
            case 'SELLER':
              router.push('/seller-dashboard')
              break
            case 'BUYER':
              router.push('/buyer-dashboard')
              break
            default:
              router.push('/dashboard')
          }
          break
        case 'orders':
          router.push('/orders')
          break
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
      return roleMap[role] || '用户'
    }

    const getRoleType = (role) => {
      const typeMap = {
        'ADMIN': 'danger',
        'SELLER': 'success',
        'BUYER': 'primary'
      }
      return typeMap[role] || 'info'
    }

const loadFeaturedProducts = async () => {
  try {
    // 首先尝试获取真实数据
    const response = await productApi.getAllProducts()
    if (response.data.code === 200) {
      featuredProducts.value = response.data.data.slice(0, 6) // 只显示前6个产品
    }
  } catch (error) {
    console.error('加载产品失败，使用模拟数据:', error)
    try {
      // 如果真实数据加载失败，使用模拟数据
      const mockResponse = await productApi.getMockProducts()
      if (mockResponse.data.code === 200) {
        featuredProducts.value = mockResponse.data.data.slice(0, 6)
      }
    } catch (mockError) {
      console.error('加载模拟数据也失败:', mockError)
    }
  }
}

onMounted(() => {
  loadFeaturedProducts()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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
  flex: 1;
  padding: 0;
}

.banner {
  margin-bottom: 60px;
}

.banner-item {
  height: 400px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.4);
}

.banner-content {
  text-align: center;
  color: white;
  z-index: 1;
  position: relative;
}

.banner-content h3 {
  font-size: 36px;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 24px;
}

.products-section, .features-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
}

.section-title {
  text-align: center;
  margin-bottom: 40px;
}

.section-title h2 {
  font-size: 32px;
  color: #333;
  margin-bottom: 16px;
}

.section-title p {
  font-size: 16px;
  color: #666;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.product-card {
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-4px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  padding: 16px 0;
}

.product-info h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.product-variety {
  color: #666;
  margin-bottom: 8px;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #E6A23C;
  margin-bottom: 8px;
}

.product-stock {
  color: #67C23A;
  margin-bottom: 16px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
}

.feature-item {
  text-align: center;
  padding: 20px;
}

.feature-item h3 {
  font-size: 20px;
  margin: 16px 0 8px;
  color: #333;
}

.feature-item p {
  color: #666;
  line-height: 1.6;
}

.footer {
  background: #333;
  color: white;
  text-align: center;
  padding: 20px;
}

.footer-content p {
  margin: 0;
}
</style>
