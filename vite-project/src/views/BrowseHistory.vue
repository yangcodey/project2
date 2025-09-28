<template>
  <div class="browse-history">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>浏览历史</h2>
      <p>查看您最近浏览过的商品</p>
    </div>

    <!-- 筛选和操作 -->
    <div class="filter-section">
      <el-card>
        <div class="filter-content">
          <div class="filter-left">
            <el-select v-model="timeFilter" placeholder="时间筛选" @change="handleTimeFilter">
              <el-option label="全部" value="all" />
              <el-option label="今天" value="today" />
              <el-option label="最近7天" value="week" />
              <el-option label="最近30天" value="month" />
            </el-select>
            
            <el-select v-model="sortBy" placeholder="排序方式" @change="handleSortChange">
              <el-option label="按浏览时间" value="browse_time" />
              <el-option label="按价格" value="price" />
              <el-option label="按评分" value="rating" />
            </el-select>
          </div>
          
          <div class="filter-right">
            <el-button @click="clearHistory" type="danger" :icon="Delete">
              清空历史
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 浏览历史列表 -->
    <div class="history-list">
      <div v-if="filteredHistory.length === 0" class="empty-state">
        <el-empty description="暂无浏览历史" />
      </div>
      
      <div v-else class="history-grid">
        <el-card v-for="item in filteredHistory" :key="item.id" class="history-card">
          <div class="history-content">
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
              
              <div class="browse-info">
                <span class="browse-time">{{ formatTime(item.browseTime) }}</span>
                <span class="browse-count">浏览 {{ item.browseCount }} 次</span>
              </div>
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
                @click="addToFavorites(item.product)"
                :icon="Star"
              >
                收藏
              </el-button>
              <el-button 
                size="small" 
                @click="removeFromHistory(item.id)"
                :icon="Delete"
              >
                删除
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredHistory.length > 0" class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 48, 96]"
        :total="totalCount"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Delete, ShoppingCart, Star } from '@element-plus/icons-vue'
import { historyApi, favoriteApi, orderApi } from '../api'

const router = useRouter()

// 响应式数据
const browseHistory = ref([])
const timeFilter = ref('all')
const sortBy = ref('browse_time')
const currentPage = ref(1)
const pageSize = ref(12)
const totalCount = ref(0)

// 计算属性
const filteredHistory = computed(() => {
  let filtered = browseHistory.value

  // 时间筛选
  if (timeFilter.value !== 'all') {
    const now = new Date()
    const filterDate = new Date()
    
    switch (timeFilter.value) {
      case 'today':
        filterDate.setHours(0, 0, 0, 0)
        break
      case 'week':
        filterDate.setDate(now.getDate() - 7)
        break
      case 'month':
        filterDate.setDate(now.getDate() - 30)
        break
    }
    
    filtered = filtered.filter(item => new Date(item.browseTime) >= filterDate)
  }

  // 排序
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'price':
        return b.product.price - a.product.price
      case 'rating':
        return (b.product.rating || 0) - (a.product.rating || 0)
      default:
        return new Date(b.browseTime) - new Date(a.browseTime)
    }
  })

  return filtered
})

// 方法
const loadBrowseHistory = async () => {
  try {
    const response = await historyApi.getBrowseHistory({
      page: currentPage.value,
      size: pageSize.value
    })
    if (response.data.code === 200) {
      browseHistory.value = response.data.data.records
      totalCount.value = response.data.data.total
    }
  } catch (error) {
    console.error('加载浏览历史失败:', error)
    ElMessage.error('加载浏览历史失败')
  }
}

const handleTimeFilter = () => {
  // 时间筛选逻辑在computed中处理
}

const handleSortChange = () => {
  // 排序逻辑在computed中处理
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadBrowseHistory()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadBrowseHistory()
}

const viewProduct = (product) => {
  // 记录浏览历史
  recordBrowseHistory(product.id)
  router.push(`/product-detail/${product.id}`)
}

const recordBrowseHistory = async (productId) => {
  try {
    await historyApi.recordBrowseHistory(productId)
  } catch (error) {
    console.error('记录浏览历史失败:', error)
  }
}

const addToCart = (product) => {
  // 实现加入购物车逻辑
  ElMessage.success('已加入购物车')
}

const addToFavorites = async (product) => {
  try {
    const response = await favoriteApi.addProductFavorite(product.id)
    if (response.data.code === 200) {
      ElMessage.success('已添加到收藏')
    }
  } catch (error) {
    console.error('添加收藏失败:', error)
    ElMessage.error('添加收藏失败')
  }
}

const removeFromHistory = async (historyId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await historyApi.removeFromHistory(historyId)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      browseHistory.value = browseHistory.value.filter(item => item.id !== historyId)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除浏览记录失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const clearHistory = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览历史吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await historyApi.clearBrowseHistory()
    if (response.data.code === 200) {
      ElMessage.success('清空历史成功')
      browseHistory.value = []
      totalCount.value = 0
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空浏览历史失败:', error)
      ElMessage.error('清空历史失败')
    }
  }
}

const formatTime = (time) => {
  const now = new Date()
  const browseTime = new Date(time)
  const diff = now - browseTime
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return browseTime.toLocaleDateString('zh-CN')
  }
}

onMounted(() => {
  loadBrowseHistory()
})
</script>

<style scoped>
.browse-history {
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

.filter-left {
  display: flex;
  gap: 15px;
  align-items: center;
}

.history-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.history-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.history-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.history-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.history-content {
  display: flex;
  flex-direction: column;
  height: 100%;
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
  flex: 1;
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
  margin-bottom: 12px;
}

.browse-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 12px;
  color: #999;
}

.product-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.product-actions .el-button {
  flex: 1;
  font-size: 12px;
}

.pagination-section {
  margin-top: 30px;
  text-align: center;
}
</style>
