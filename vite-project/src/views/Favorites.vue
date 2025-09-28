<template>
  <div class="favorites">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的收藏</h2>
      <p>收藏的商品和店铺</p>
    </div>

    <!-- 筛选和排序 -->
    <div class="filter-section">
      <el-card>
        <div class="filter-content">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="商品收藏" name="products" />
            <el-tab-pane label="店铺收藏" name="sellers" />
          </el-tabs>
          
          <div class="filter-actions">
            <el-select v-model="sortBy" placeholder="排序方式" @change="handleSortChange">
              <el-option label="按收藏时间" value="created_at" />
              <el-option label="按价格" value="price" />
              <el-option label="按评分" value="rating" />
            </el-select>
            
            <el-button @click="clearAllFavorites" type="danger" :icon="Delete">
              清空收藏
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 商品收藏列表 -->
    <div v-if="activeTab === 'products'" class="favorites-list">
      <div v-if="productFavorites.length === 0" class="empty-state">
        <el-empty description="暂无收藏的商品" />
      </div>
      
      <div v-else class="products-grid">
        <el-card v-for="item in productFavorites" :key="item.id" class="product-card">
          <div class="product-image" @click="viewProduct(item.product)">
            <img :src="item.product.imageUrl || '/default-product.jpg'" :alt="item.product.name" />
            <div class="product-overlay">
              <el-button type="primary" size="small" :icon="View">查看详情</el-button>
            </div>
          </div>
          
          <div class="product-info">
            <h4 class="product-name" @click="viewProduct(item.product)">{{ item.product.name }}</h4>
            <p class="product-variety">{{ item.product.variety }}</p>
            <p class="product-spec">{{ item.product.specification }}</p>
            
            <div class="product-price">
              <span class="price">¥{{ item.product.price }}</span>
              <span class="unit">/件</span>
            </div>
            
            <div class="product-stock">
              <el-tag :type="item.product.stockQuantity > 10 ? 'success' : item.product.stockQuantity > 0 ? 'warning' : 'danger'">
                库存: {{ item.product.stockQuantity }}
              </el-tag>
            </div>
            
            <div class="product-actions">
              <el-button 
                type="primary" 
                size="small"
                @click="addToCart(item.product)"
                :disabled="item.product.stockQuantity <= 0"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button 
                size="small" 
                @click="removeFavorite(item.id, 'product')"
                :icon="StarFilled"
              >
                取消收藏
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 店铺收藏列表 -->
    <div v-if="activeTab === 'sellers'" class="favorites-list">
      <div v-if="sellerFavorites.length === 0" class="empty-state">
        <el-empty description="暂无收藏的店铺" />
      </div>
      
      <div v-else class="sellers-grid">
        <el-card v-for="item in sellerFavorites" :key="item.id" class="seller-card">
          <div class="seller-header">
            <el-avatar :size="60" :src="item.seller.avatar">
              {{ item.seller.realName?.charAt(0) }}
            </el-avatar>
            <div class="seller-info">
              <h4 class="seller-name">{{ item.seller.realName }}</h4>
              <p class="seller-desc">{{ item.seller.description || '暂无描述' }}</p>
              <div class="seller-stats">
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ item.seller.rating || 0 }}分
                </span>
                <span class="stat-item">
                  <el-icon><Goods /></el-icon>
                  {{ item.seller.productCount || 0 }}个商品
                </span>
              </div>
            </div>
          </div>
          
          <div class="seller-actions">
            <el-button type="primary" @click="viewSeller(item.seller.id)">
              <el-icon><View /></el-icon>
              进入店铺
            </el-button>
            <el-button @click="removeFavorite(item.id, 'seller')" :icon="StarFilled">
              取消收藏
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Delete, ShoppingCart, Star, StarFilled, Goods } from '@element-plus/icons-vue'
import { favoriteApi, productApi, orderApi } from '../api'

const router = useRouter()

// 响应式数据
const activeTab = ref('products')
const sortBy = ref('created_at')
const productFavorites = ref([])
const sellerFavorites = ref([])

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const loadFavorites = async () => {
  try {
    // 加载商品收藏
    const productResponse = await favoriteApi.getProductFavorites()
    if (productResponse.data.code === 200) {
      productFavorites.value = productResponse.data.data
    }
    
    // 加载店铺收藏
    const sellerResponse = await favoriteApi.getSellerFavorites()
    if (sellerResponse.data.code === 200) {
      sellerFavorites.value = sellerResponse.data.data
    }
  } catch (error) {
    console.error('加载收藏失败:', error)
    ElMessage.error('加载收藏失败')
  }
}

const handleTabChange = (tab) => {
  activeTab.value = tab
}

const handleSortChange = () => {
  // 实现排序逻辑
  sortFavorites()
}

const sortFavorites = () => {
  if (activeTab.value === 'products') {
    productFavorites.value.sort((a, b) => {
      switch (sortBy.value) {
        case 'price':
          return b.product.price - a.product.price
        case 'rating':
          return (b.product.rating || 0) - (a.product.rating || 0)
        default:
          return new Date(b.createdAt) - new Date(a.createdAt)
      }
    })
  } else {
    sellerFavorites.value.sort((a, b) => {
      switch (sortBy.value) {
        case 'rating':
          return (b.seller.rating || 0) - (a.seller.rating || 0)
        default:
          return new Date(b.createdAt) - new Date(a.createdAt)
      }
    })
  }
}

const viewProduct = (product) => {
  router.push(`/product-detail/${product.id}`)
}

const viewSeller = (sellerId) => {
  router.push(`/seller-shop/${sellerId}`)
}

const addToCart = (product) => {
  // 实现加入购物车逻辑
  ElMessage.success('已加入购物车')
}

const removeFavorite = async (favoriteId, type) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await favoriteApi.removeFavorite(favoriteId)
    if (response.data.code === 200) {
      ElMessage.success('取消收藏成功')
      if (type === 'product') {
        productFavorites.value = productFavorites.value.filter(item => item.id !== favoriteId)
      } else {
        sellerFavorites.value = sellerFavorites.value.filter(item => item.id !== favoriteId)
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

const clearAllFavorites = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有收藏吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await favoriteApi.clearAllFavorites()
    if (response.data.code === 200) {
      ElMessage.success('清空收藏成功')
      productFavorites.value = []
      sellerFavorites.value = []
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空收藏失败:', error)
      ElMessage.error('清空收藏失败')
    }
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.favorites-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 15px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-image:hover img {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-image:hover .product-overlay {
  opacity: 1;
}

.product-info {
  padding: 0 5px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
  cursor: pointer;
}

.product-name:hover {
  color: #409EFF;
}

.product-variety {
  color: #666;
  margin-bottom: 4px;
  font-size: 14px;
}

.product-spec {
  color: #999;
  font-size: 12px;
  margin-bottom: 12px;
}

.product-price {
  margin-bottom: 12px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #E6A23C;
}

.unit {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}

.product-stock {
  margin-bottom: 15px;
}

.product-actions {
  display: flex;
  gap: 8px;
}

.product-actions .el-button {
  flex: 1;
  font-size: 12px;
}

.sellers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.seller-card {
  margin-bottom: 0;
}

.seller-header {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.seller-info {
  flex: 1;
}

.seller-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.seller-desc {
  color: #666;
  margin-bottom: 12px;
  font-size: 14px;
  line-height: 1.4;
}

.seller-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
}

.seller-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.seller-actions .el-button {
  font-size: 12px;
}
</style>
