<template>
  <div class="advanced-search">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>高级搜索</h2>
      <p>使用多种条件精确搜索商品</p>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-card>
        <el-form :model="searchForm" label-width="100px" @submit.prevent="handleSearch">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="关键词">
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="输入商品名称、品种、描述等关键词"
                  @input="handleKeywordInput"
                  @keyup.enter="handleSearch"
                >
                  <template #append>
                    <el-button @click="handleSearch" :icon="Search">搜索</el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            
            <el-col :span="6">
              <el-form-item label="品种">
                <el-select v-model="searchForm.variety" placeholder="选择品种" clearable>
                  <el-option label="全部" value="" />
                  <el-option label="鸡" value="鸡" />
                  <el-option label="鸭" value="鸭" />
                  <el-option label="鹅" value="鹅" />
                  <el-option label="猪" value="猪" />
                  <el-option label="牛" value="牛" />
                  <el-option label="羊" value="羊" />
                </el-select>
              </el-form-item>
            </el-col>
            
            <el-col :span="6">
              <el-form-item label="销售方">
                <el-select v-model="searchForm.sellerId" placeholder="选择销售方" clearable>
                  <el-option label="全部" value="" />
                  <el-option
                    v-for="seller in sellers"
                    :key="seller.id"
                    :label="seller.realName"
                    :value="seller.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="价格区间">
                <el-slider
                  v-model="priceRange"
                  range
                  :min="0"
                  :max="1000"
                  :step="10"
                  @change="handlePriceChange"
                />
                <div class="price-display">
                  ¥{{ priceRange[0] }} - ¥{{ priceRange[1] }}
                </div>
              </el-form-item>
            </el-col>
            
            <el-col :span="8">
              <el-form-item label="库存状态">
                <el-radio-group v-model="searchForm.stockStatus">
                  <el-radio label="">全部</el-radio>
                  <el-radio label="in_stock">有库存</el-radio>
                  <el-radio label="low_stock">库存不足</el-radio>
                  <el-radio label="out_of_stock">缺货</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            
            <el-col :span="8">
              <el-form-item label="评分">
                <el-rate
                  v-model="searchForm.minRating"
                  :max="5"
                  show-text
                  :texts="['不限', '1星以上', '2星以上', '3星以上', '4星以上', '5星']"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="排序方式">
                <el-select v-model="searchForm.sortBy" placeholder="选择排序方式">
                  <el-option label="默认排序" value="default" />
                  <el-option label="价格从低到高" value="price_asc" />
                  <el-option label="价格从高到低" value="price_desc" />
                  <el-option label="评分从高到低" value="rating_desc" />
                  <el-option label="销量从高到低" value="sales_desc" />
                  <el-option label="最新上架" value="newest" />
                </el-select>
              </el-form-item>
            </el-col>
            
            <el-col :span="8">
              <el-form-item label="标签">
                <el-input
                  v-model="searchForm.tags"
                  placeholder="输入标签，用逗号分隔"
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="8">
              <el-form-item label="上架时间">
                <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary" @click="handleSearch" :loading="loading" :icon="Search">
              搜索
            </el-button>
            <el-button @click="resetForm" :icon="Refresh">重置</el-button>
            <el-button @click="saveSearch" :icon="Star">保存搜索</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 搜索建议 -->
    <div v-if="searchSuggestions.length > 0" class="search-suggestions">
      <el-card>
        <h4>搜索建议</h4>
        <div class="suggestions-list">
          <el-tag
            v-for="suggestion in searchSuggestions"
            :key="suggestion"
            @click="applySuggestion(suggestion)"
            class="suggestion-tag"
          >
            {{ suggestion }}
          </el-tag>
        </div>
      </el-card>
    </div>

    <!-- 搜索历史 -->
    <div v-if="searchHistory.length > 0" class="search-history">
      <el-card>
        <div class="history-header">
          <h4>搜索历史</h4>
          <el-button size="small" @click="clearHistory" :icon="Delete">清空</el-button>
        </div>
        <div class="history-list">
          <el-tag
            v-for="(item, index) in searchHistory"
            :key="index"
            @click="applyHistoryItem(item)"
            class="history-tag"
            closable
            @close="removeHistoryItem(index)"
          >
            {{ item.keyword }}
          </el-tag>
        </div>
      </el-card>
    </div>

    <!-- 搜索结果 -->
    <div v-if="searchResults.length > 0" class="search-results">
      <div class="results-header">
        <h3>搜索结果 ({{ totalCount }} 个商品)</h3>
        <div class="results-actions">
          <el-button-group>
            <el-button :type="viewMode === 'grid' ? 'primary' : ''" @click="viewMode = 'grid'" :icon="Grid">
              网格
            </el-button>
            <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'" :icon="List">
              列表
            </el-button>
          </el-button-group>
        </div>
      </div>

      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="products-grid">
        <el-card
          v-for="product in searchResults"
          :key="product.id"
          class="product-card"
          @click="viewProduct(product)"
        >
          <img
            :src="product.imageUrl || '/default-product.jpg'"
            :alt="product.name"
            class="product-image"
          />
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-variety">{{ product.variety }}</p>
            <p class="product-spec">{{ product.specification }}</p>
            <div class="product-price">¥{{ product.price }}</div>
            <div class="product-stock">
              <el-tag :type="getStockType(product.stockQuantity)">
                库存: {{ product.stockQuantity }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 列表视图 -->
      <div v-else class="products-list">
        <el-table :data="searchResults" style="width: 100%">
          <el-table-column prop="name" label="商品名称" width="200" />
          <el-table-column prop="variety" label="品种" width="100" />
          <el-table-column prop="specification" label="规格" width="150" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="stockQuantity" label="库存" width="100" />
          <el-table-column prop="sellerName" label="销售方" width="120" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="viewProduct(row)">查看</el-button>
              <el-button size="small" @click="addToCart(row)">加购物车</el-button>
              <el-button size="small" @click="addToFavorites(row)">收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalCount"
          :page-sizes="[12, 24, 48, 96]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && searchResults.length === 0 && hasSearched" class="empty-results">
      <el-empty description="没有找到相关商品">
        <el-button type="primary" @click="resetForm">重新搜索</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Star, Delete, Grid, List } from '@element-plus/icons-vue'
import { productApi, userApi, favoriteApi } from '../api'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const searchResults = ref([])
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const viewMode = ref('grid')
const hasSearched = ref(false)

// 搜索表单
const searchForm = ref({
  keyword: '',
  variety: '',
  sellerId: '',
  minPrice: 0,
  maxPrice: 1000,
  stockStatus: '',
  minRating: 0,
  sortBy: 'default',
  tags: '',
  dateRange: []
})

const priceRange = ref([0, 1000])

// 搜索建议和历史
const searchSuggestions = ref([])
const searchHistory = ref([])
const sellers = ref([])

// 方法
const loadSellers = async () => {
  try {
    const response = await userApi.getUsersByRole('SELLER')
    if (response.data.code === 200) {
      sellers.value = response.data.data
    }
  } catch (error) {
    console.error('加载销售方失败:', error)
  }
}

const loadSearchHistory = () => {
  const history = localStorage.getItem('searchHistory')
  if (history) {
    searchHistory.value = JSON.parse(history)
  }
}

const saveSearchHistory = (keyword) => {
  if (!keyword.trim()) return
  
  const history = searchHistory.value.filter(item => item.keyword !== keyword)
  history.unshift({ keyword, timestamp: new Date().toISOString() })
  
  if (history.length > 10) {
    history.splice(10)
  }
  
  searchHistory.value = history
  localStorage.setItem('searchHistory', JSON.stringify(history))
}

const handleKeywordInput = async (value) => {
  if (value.length < 2) {
    searchSuggestions.value = []
    return
  }
  
  try {
    // 这里应该调用搜索建议API
    const suggestions = await getSearchSuggestions(value)
    searchSuggestions.value = suggestions
  } catch (error) {
    console.error('获取搜索建议失败:', error)
  }
}

const getSearchSuggestions = async (keyword) => {
  // 模拟搜索建议
  const mockSuggestions = [
    '散养土鸡',
    '生态鸡蛋',
    '有机猪肉',
    '高山羊肉',
    '新鲜鸭蛋'
  ]
  
  return mockSuggestions.filter(item => 
    item.toLowerCase().includes(keyword.toLowerCase())
  ).slice(0, 5)
}

const handlePriceChange = (value) => {
  searchForm.value.minPrice = value[0]
  searchForm.value.maxPrice = value[1]
}

const handleSearch = async () => {
  if (!searchForm.value.keyword.trim() && !searchForm.value.variety && !searchForm.value.sellerId) {
    ElMessage.warning('请输入搜索条件')
    return
  }
  
  try {
    loading.value = true
    hasSearched.value = true
    
    // 保存搜索历史
    if (searchForm.value.keyword.trim()) {
      saveSearchHistory(searchForm.value.keyword)
    }
    
    const params = {
      ...searchForm.value,
      page: currentPage.value,
      size: pageSize.value
    }
    
    const response = await productApi.advancedSearch(params)
    if (response.data.code === 200) {
      searchResults.value = response.data.data.records || response.data.data
      totalCount.value = response.data.data.total || searchResults.value.length
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  searchForm.value = {
    keyword: '',
    variety: '',
    sellerId: '',
    minPrice: 0,
    maxPrice: 1000,
    stockStatus: '',
    minRating: 0,
    sortBy: 'default',
    tags: '',
    dateRange: []
  }
  priceRange.value = [0, 1000]
  searchResults.value = []
  totalCount.value = 0
  hasSearched.value = false
  searchSuggestions.value = []
}

const saveSearch = () => {
  ElMessage.success('搜索条件已保存')
  // 这里可以实现保存搜索条件的功能
}

const applySuggestion = (suggestion) => {
  searchForm.value.keyword = suggestion
  searchSuggestions.value = []
  handleSearch()
}

const applyHistoryItem = (item) => {
  searchForm.value.keyword = item.keyword
  handleSearch()
}

const removeHistoryItem = (index) => {
  searchHistory.value.splice(index, 1)
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
}

const clearHistory = () => {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
  ElMessage.success('搜索历史已清空')
}

const viewProduct = (product) => {
  router.push(`/product-detail/${product.id}`)
}

const addToCart = (product) => {
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

const getStockType = (stock) => {
  if (stock > 10) return 'success'
  if (stock > 0) return 'warning'
  return 'danger'
}

const handleSizeChange = (size) => {
  pageSize.value = size
  handleSearch()
}

const handlePageChange = (page) => {
  currentPage.value = page
  handleSearch()
}

onMounted(() => {
  loadSellers()
  loadSearchHistory()
})
</script>

<style scoped>
.advanced-search {
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

.search-form {
  margin-bottom: 30px;
}

.price-display {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}

.search-suggestions,
.search-history {
  margin-bottom: 20px;
}

.suggestions-list,
.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.suggestion-tag,
.history-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.suggestion-tag:hover,
.history-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.search-results {
  margin-top: 30px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
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
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 15px;
}

.product-info {
  padding: 0 5px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
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
  font-size: 18px;
  font-weight: bold;
  color: #E6A23C;
  margin-bottom: 12px;
}

.product-stock {
  margin-bottom: 15px;
}

.products-list {
  margin-bottom: 30px;
}

.pagination-section {
  text-align: center;
}

.empty-results {
  text-align: center;
  padding: 60px 0;
}
</style>
