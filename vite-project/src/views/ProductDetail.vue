<template>
  <div class="product-detail">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>侬帮我养</h2>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="'products'"
          class="nav-menu"
        >
          <el-menu-item index="home" @click="goToHome">首页</el-menu-item>
          <el-menu-item index="products" @click="goToProducts">商品</el-menu-item>
          <el-menu-item index="orders" @click="goToOrders">订单</el-menu-item>
          <el-menu-item index="dashboard" @click="goToDashboard">个人中心</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <NotificationCenter />
          <el-dropdown @command="handleUserCommand" v-if="userInfo.id">
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
          <div v-else class="login-actions">
            <el-button @click="goToLogin">登录</el-button>
            <el-button type="primary" @click="goToRegister">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <div class="product-container" v-loading="loading">
        <div v-if="product" class="product-info">
          <!-- 面包屑导航 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item @click="goToHome">首页</el-breadcrumb-item>
            <el-breadcrumb-item @click="goToProducts">商品列表</el-breadcrumb-item>
            <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
          </el-breadcrumb>

          <el-row :gutter="40" class="product-content">
            <!-- 产品图片 -->
            <el-col :span="12">
              <div class="product-image">
                <el-image
                  :src="product.imageUrl || '/default-product.jpg'"
                  :alt="product.name"
                  fit="cover"
                  class="main-image"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>暂无图片</span>
                    </div>
                  </template>
                </el-image>
              </div>
            </el-col>

            <!-- 产品信息 -->
            <el-col :span="12">
              <div class="product-details">
                <h1 class="product-title">{{ product.name }}</h1>
                <div class="product-meta">
                  <div class="price-section">
                    <span class="current-price">¥{{ product.price }}</span>
                    <span class="price-unit">/件</span>
                  </div>
                  <div class="stock-info">
                    <span class="stock-label">库存：</span>
                    <span class="stock-quantity" :class="{ 'low-stock': product.stockQuantity < 10 }">
                      {{ product.stockQuantity }}件
                    </span>
                  </div>
                </div>

                <div class="product-description">
                  <h3>产品描述</h3>
                  <p>{{ product.description || '暂无描述' }}</p>
                </div>

                <div class="product-specs">
                  <h3>产品规格</h3>
                  <el-descriptions :column="2" border>
                    <el-descriptions-item label="品种">{{ product.variety || '暂无' }}</el-descriptions-item>
                    <el-descriptions-item label="销售方">{{ sellerName }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                      <el-tag :type="getStatusType(product.status)">
                        {{ getStatusText(product.status) }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="上架时间">{{ formatDate(product.createdAt) }}</el-descriptions-item>
                  </el-descriptions>
                </div>

                <!-- 购买操作 -->
                <div class="purchase-section" v-if="product.status === 1 && product.stockQuantity > 0">
                  <div class="quantity-selector">
                    <span class="quantity-label">数量：</span>
                    <el-input-number
                      v-model="quantity"
                      :min="1"
                      :max="product.stockQuantity"
                      controls-position="right"
                      size="large"
                    />
                  </div>
                  <div class="action-buttons">
                    <el-button
                      type="primary"
                      size="large"
                      @click="addToCart"
                      :disabled="!userInfo.id"
                    >
                      <el-icon><ShoppingCart /></el-icon>
                      加入购物车
                    </el-button>
                    <el-button
                      type="success"
                      size="large"
                      @click="quickBuy"
                      :disabled="!userInfo.id"
                    >
                      <el-icon><CreditCard /></el-icon>
                      立即购买
                    </el-button>
                  </div>
                </div>

                <div class="unavailable-section" v-else>
                  <el-alert
                    :title="product.stockQuantity <= 0 ? '商品暂时缺货' : '商品暂时不可购买'"
                    type="warning"
                    show-icon
                    :closable="false"
                  />
                </div>
              </div>
            </el-col>
          </el-row>

          <!-- 产品评价 -->
          <div class="reviews-section">
            <el-tabs v-model="activeTab" class="reviews-tabs">
              <el-tab-pane label="商品评价" name="reviews">
                <div class="reviews-header">
                  <h3>用户评价</h3>
                  <el-button type="primary" @click="goToReviews" v-if="userInfo.id">
                    查看全部评价
                  </el-button>
                </div>
                <div class="reviews-content">
                  <div v-if="reviews.length === 0" class="no-reviews">
                    <el-empty description="暂无评价" />
                  </div>
                  <div v-else>
                    <div
                      v-for="review in reviews.slice(0, 3)"
                      :key="review.id"
                      class="review-item"
                    >
                      <div class="review-header">
                        <el-avatar :size="40">{{ review.userName?.charAt(0) }}</el-avatar>
                        <div class="review-info">
                          <div class="reviewer-name">{{ review.userName }}</div>
                          <div class="review-rating">
                            <el-rate
                              v-model="review.rating"
                              disabled
                              show-score
                              text-color="#ff9900"
                            />
                          </div>
                        </div>
                        <div class="review-time">{{ formatDate(review.createTime) }}</div>
                      </div>
                      <div class="review-content">
                        <p>{{ review.comment }}</p>
                        <div v-if="review.imageUrls && review.imageUrls.length > 0" class="review-images">
                          <el-image
                            v-for="(imageUrl, index) in review.imageUrls"
                            :key="index"
                            :src="imageUrl"
                            :preview-src-list="review.imageUrls"
                            class="review-image"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <div v-else class="not-found">
          <el-result
            icon="warning"
            title="商品不存在"
            sub-title="抱歉，您访问的商品不存在或已被删除"
          >
            <template #extra>
              <el-button type="primary" @click="goToProducts">返回商品列表</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, CreditCard, Picture } from '@element-plus/icons-vue'
import { productApi, reviewApi } from '../api'
import NotificationCenter from '../components/NotificationCenter.vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const product = ref(null)
const sellerName = ref('')
const reviews = ref([])
const quantity = ref(1)
const activeTab = ref('reviews')

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const goToHome = () => router.push('/home')
const goToProducts = () => router.push('/products')
const goToOrders = () => router.push('/orders')
const goToDashboard = () => router.push('/dashboard')
const goToLogin = () => router.push('/login')
const goToRegister = () => router.push('/register')
const goToReviews = () => router.push(`/product-reviews/${route.params.id}`)

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

const loadProduct = async () => {
  try {
    loading.value = true
    const productId = route.params.id
    const response = await productApi.getProductById(productId)
    
    if (response.data.code === 200) {
      product.value = response.data.data
      await loadSellerInfo()
      await loadReviews()
    } else {
      ElMessage.error('商品不存在')
      product.value = null
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败')
    product.value = null
  } finally {
    loading.value = false
  }
}

const loadSellerInfo = async () => {
  if (!product.value?.sellerId) return
  
  try {
    // 这里应该调用获取用户信息的API
    // 暂时使用模拟数据
    const sellerMap = {
      2: '张三农场',
      3: '李四养殖场'
    }
    sellerName.value = sellerMap[product.value.sellerId] || '未知销售方'
  } catch (error) {
    console.error('加载销售方信息失败:', error)
    sellerName.value = '未知销售方'
  }
}

const loadReviews = async () => {
  try {
    const productId = route.params.id
    const response = await reviewApi.getProductReviews(productId)
    
    if (response.data.code === 200) {
      reviews.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载评价失败:', error)
    // 使用模拟数据
    reviews.value = [
      {
        id: 1,
        userName: '王五',
        rating: 5,
        comment: '商品质量很好，包装也很仔细，推荐购买！',
        imageUrls: [],
        createTime: '2024-01-02 10:00:00'
      },
      {
        id: 2,
        userName: '赵六',
        rating: 4,
        comment: '整体不错，性价比很高。',
        imageUrls: [],
        createTime: '2024-01-03 11:00:00'
      }
    ]
  }
}

const addToCart = () => {
  if (!userInfo.value.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  // 这里实现加入购物车逻辑
  ElMessage.success(`已将 ${quantity.value} 件商品加入购物车`)
}

const quickBuy = () => {
  if (!userInfo.value.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  // 这里实现立即购买逻辑
  ElMessage.success('跳转到订单页面')
}

const getStatusText = (status) => {
  const statusMap = {
    1: '上架中',
    0: '已下架',
    2: '待审核',
    3: '已拒绝',
    4: '售罄'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    1: 'success',
    0: 'info',
    2: 'warning',
    3: 'danger',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail {
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

.login-actions {
  display: flex;
  gap: 10px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.breadcrumb {
  margin-bottom: 20px;
}

.breadcrumb .el-breadcrumb__item {
  cursor: pointer;
}

.product-content {
  margin-bottom: 40px;
}

.product-image {
  text-align: center;
}

.main-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.image-error .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.product-details {
  padding-left: 20px;
}

.product-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0 0 20px 0;
}

.product-meta {
  margin-bottom: 30px;
}

.price-section {
  margin-bottom: 15px;
}

.current-price {
  font-size: 32px;
  font-weight: bold;
  color: #f56c6c;
}

.price-unit {
  font-size: 16px;
  color: #666;
  margin-left: 5px;
}

.stock-info {
  font-size: 16px;
}

.stock-label {
  color: #666;
}

.stock-quantity {
  color: #67c23a;
  font-weight: bold;
}

.stock-quantity.low-stock {
  color: #f56c6c;
}

.product-description,
.product-specs {
  margin-bottom: 30px;
}

.product-description h3,
.product-specs h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

.product-description p {
  line-height: 1.6;
  color: #666;
}

.purchase-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.quantity-label {
  margin-right: 10px;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.unavailable-section {
  margin-bottom: 30px;
}

.reviews-section {
  border-top: 1px solid #e4e7ed;
  padding-top: 30px;
}

.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.reviews-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.reviews-content {
  min-height: 200px;
}

.no-reviews {
  text-align: center;
  padding: 40px 0;
}

.review-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.review-info {
  flex: 1;
  margin-left: 15px;
}

.reviewer-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.review-time {
  color: #999;
  font-size: 14px;
}

.review-content p {
  line-height: 1.6;
  color: #333;
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
}

.not-found {
  text-align: center;
  padding: 60px 0;
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
  
  .main-content {
    padding: 15px;
  }
  
  .product-container {
    padding: 20px;
  }
  
  .product-content {
    flex-direction: column;
  }
  
  .product-details {
    padding-left: 0;
    margin-top: 20px;
  }
  
  .product-title {
    font-size: 24px;
  }
  
  .current-price {
    font-size: 28px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .review-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .review-info {
    margin-left: 0;
    margin-top: 10px;
  }
}
</style>
