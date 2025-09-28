<template>
  <div class="products-page">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>侬帮我养</h2>
          <p>在线销售预约系统</p>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="'products'"
          class="nav-menu"
          @select="handleSelect"
        >
          <el-menu-item index="home">首页</el-menu-item>
          <el-menu-item index="products">产品展示</el-menu-item>
          <el-menu-item index="about">关于我们</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <el-button v-if="!isLoggedIn" @click="goToLogin" type="primary">登录</el-button>
          <el-button v-if="!isLoggedIn" @click="goToRegister" type="success">注册</el-button>
          <div v-if="isLoggedIn" class="user-section">
            <!-- 通知中心 -->
            <NotificationCenter />
            
            <!-- 购物车 -->
            <el-badge :value="cartItems.length" class="cart-badge">
              <el-button @click="showCart = true" :icon="ShoppingCart" circle />
            </el-badge>
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.realName?.charAt(0) }}</el-avatar>
                <span class="username">{{ userInfo.realName }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="dashboard">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 搜索和筛选 -->
      <div class="search-section">
        <div class="search-container">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索产品名称"
            class="search-input"
            @keyup.enter="handleSearch"
            @input="handleSearchInput"
            @focus="showSuggestions = true"
            @blur="hideSuggestions"
          >
            <template #append>
              <el-button @click="handleSearch" :icon="Search">搜索</el-button>
            </template>
            <template #suffix>
              <el-button @click="goToAdvancedSearch" :icon="Setting" text>高级搜索</el-button>
            </template>
          </el-input>
          
          <!-- 搜索建议 -->
          <div v-if="showSuggestions && searchSuggestions.length > 0" class="search-suggestions">
            <div
              v-for="suggestion in searchSuggestions"
              :key="suggestion"
              class="suggestion-item"
              @click="applySuggestion(suggestion)"
            >
              <el-icon><Search /></el-icon>
              <span>{{ suggestion }}</span>
            </div>
          </div>
          
          <div class="filter-section">
            <el-select v-model="selectedVariety" placeholder="选择品种" @change="handleFilter">
              <el-option label="全部品种" value="" />
              <el-option label="鸡" value="鸡" />
              <el-option label="鸭" value="鸭" />
              <el-option label="鹅" value="鹅" />
              <el-option label="猪" value="猪" />
              <el-option label="牛" value="牛" />
              <el-option label="羊" value="羊" />
            </el-select>
            
            <el-slider
              v-model="priceRange"
              range
              :min="0"
              :max="1000"
              :step="10"
              @change="handleFilter"
              class="price-slider"
            />
            <span class="price-label">价格区间: ¥{{ priceRange[0] }} - ¥{{ priceRange[1] }}</span>
          </div>
        </div>
      </div>

      <!-- 产品列表 -->
      <div class="products-container">
        <div class="products-grid" v-loading="loading">
          <el-card
            v-for="product in filteredProducts"
            :key="product.id"
            class="product-card"
            shadow="hover"
            @click="viewProductDetail(product)"
          >
            <img
              :src="product.imageUrl || '/default-product.jpg'"
              :alt="product.name"
              class="product-image"
            />
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-variety">{{ product.variety }}</p>
              <p class="product-spec">{{ product.specification }}</p>
              <div class="product-price">
                <span class="price">¥{{ product.price }}</span>
                <span class="unit">/件</span>
              </div>
              <div class="product-stock">
                <el-tag :type="product.stockQuantity > 10 ? 'success' : product.stockQuantity > 0 ? 'warning' : 'danger'">
                  库存: {{ product.stockQuantity }}
                </el-tag>
              </div>
              <div class="product-actions">
                <el-button 
                  type="primary" 
                  @click.stop="quickBuy(product)"
                  :disabled="product.stockQuantity <= 0"
                >
                  <el-icon><ShoppingCart /></el-icon>
                  立即购买
                </el-button>
                <el-button 
                  @click.stop="addToCart(product)"
                  :disabled="product.stockQuantity <= 0"
                >
                  <el-icon><Plus /></el-icon>
                  加入购物车
                </el-button>
                <el-button @click.stop="viewProductDetail(product)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button @click.stop="viewProductReviews(product.id)">
                  <el-icon><Star /></el-icon>
                  评价
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
        
        <!-- 空状态 -->
        <el-empty v-if="!loading && filteredProducts.length === 0" description="暂无产品" />
      </div>
    </el-main>

    <!-- 产品详情对话框 -->
    <el-dialog
      v-model="productDetailVisible"
      :title="selectedProduct?.name"
      width="800px"
      @close="closeProductDetail"
    >
      <div v-if="selectedProduct" class="product-detail">
        <div class="detail-image">
          <img :src="selectedProduct.imageUrl || '/default-product.jpg'" :alt="selectedProduct.name" />
        </div>
        <div class="detail-info">
          <h3>{{ selectedProduct.name }}</h3>
          <p><strong>品种:</strong> {{ selectedProduct.variety }}</p>
          <p><strong>规格:</strong> {{ selectedProduct.specification }}</p>
          <p><strong>价格:</strong> ¥{{ selectedProduct.price }}</p>
          <p><strong>库存:</strong> {{ selectedProduct.stockQuantity }}</p>
          <p><strong>养殖周期:</strong> {{ selectedProduct.breedingCycle }}天</p>
          <p><strong>描述:</strong> {{ selectedProduct.description }}</p>
          
          <div class="order-section" v-if="isLoggedIn">
            <el-form :model="orderForm" label-width="80px">
              <el-form-item label="预约数量">
                <el-input-number
                  v-model="orderForm.quantity"
                  :min="1"
                  :max="selectedProduct.stockQuantity"
                  controls-position="right"
                />
              </el-form-item>
              <el-form-item label="收货地址">
                <el-input
                  v-model="orderForm.deliveryAddress"
                  type="textarea"
                  placeholder="请输入收货地址"
                />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input
                  v-model="orderForm.contactPhone"
                  placeholder="请输入联系电话"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeProductDetail">取消</el-button>
          <el-button type="primary" @click="createOrder" :disabled="!isLoggedIn">
            确认预约
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 购物车对话框 -->
    <el-dialog
      v-model="showCart"
      title="购物车"
      width="600px"
    >
      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-empty description="购物车为空" />
      </div>
      <div v-else>
        <el-table :data="cartItems" style="width: 100%">
          <el-table-column prop="name" label="产品名称" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="120">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="row.stockQuantity"
                size="small"
                @change="updateCartItem(row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="total" label="小计" width="100">
            <template #default="{ row }">
              ¥{{ (row.price * row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="removeFromCart(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="cart-total">
          <h3>总计: ¥{{ cartTotal.toFixed(2) }}</h3>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showCart = false">关闭</el-button>
        <el-button 
          v-if="cartItems.length > 0" 
          type="primary" 
          @click="checkout"
        >
          结算 ({{ cartItems.length }}件商品)
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, ShoppingCart, View, Plus, Star, Setting } from '@element-plus/icons-vue'
import { productApi, orderApi } from '../api'
import NotificationCenter from '../components/NotificationCenter.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const products = ref([])
const searchKeyword = ref('')
const selectedVariety = ref('')
const priceRange = ref([0, 1000])
const productDetailVisible = ref(false)
const selectedProduct = ref(null)
const orderForm = ref({
  quantity: 1,
  deliveryAddress: '',
  contactPhone: ''
})
const showCart = ref(false)
const cartItems = ref([])
const showSuggestions = ref(false)
const searchSuggestions = ref([])

// 计算属性
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') && localStorage.getItem('user')
})

const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

const filteredProducts = computed(() => {
  let filtered = products.value

  // 按关键词搜索
  if (searchKeyword.value) {
    filtered = filtered.filter(product =>
      product.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 按品种筛选
  if (selectedVariety.value) {
    filtered = filtered.filter(product => product.variety === selectedVariety.value)
  }

  // 按价格区间筛选
  filtered = filtered.filter(product =>
    product.price >= priceRange.value[0] && product.price <= priceRange.value[1]
  )

  return filtered
})

const cartTotal = computed(() => {
  return cartItems.value.reduce((total, item) => total + (item.price * item.quantity), 0)
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
    case 'about':
      break
  }
}

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'dashboard':
      router.push('/dashboard')
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
      router.push('/')
      break
  }
}

const handleSearch = () => {
  showSuggestions.value = false
  // 搜索逻辑在computed中处理
}

const handleFilter = () => {
  // 筛选逻辑在computed中处理
}

const handleSearchInput = async (value) => {
  if (value.length < 2) {
    searchSuggestions.value = []
    return
  }
  
  try {
    const response = await productApi.getSearchSuggestions(value)
    if (response.data.code === 200) {
      searchSuggestions.value = response.data.data.slice(0, 5)
    }
  } catch (error) {
    console.error('获取搜索建议失败:', error)
  }
}

const applySuggestion = (suggestion) => {
  searchKeyword.value = suggestion
  showSuggestions.value = false
  handleSearch()
}

const hideSuggestions = () => {
  setTimeout(() => {
    showSuggestions.value = false
  }, 200)
}

const loadProducts = async () => {
  try {
    loading.value = true
    const response = await productApi.getAllProducts()
    if (response.data.code === 200) {
      products.value = response.data.data
    }
  } catch (error) {
    console.error('加载产品失败:', error)
    ElMessage.error('加载产品失败')
  } finally {
    loading.value = false
  }
}

const viewProductDetail = (product) => {
  router.push(`/products/${product.id}`)
}

const closeProductDetail = () => {
  productDetailVisible.value = false
  selectedProduct.value = null
}

const addToCart = (product) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 检查是否已在购物车中
  const existingItem = cartItems.value.find(item => item.id === product.id)
  if (existingItem) {
    if (existingItem.quantity < product.stockQuantity) {
      existingItem.quantity += 1
      ElMessage.success('已更新购物车')
    } else {
      ElMessage.warning('库存不足')
    }
  } else {
    cartItems.value.push({
      ...product,
      quantity: 1
    })
    ElMessage.success('已添加到购物车')
  }
}

const quickBuy = (product) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  viewProductDetail(product)
}

const updateCartItem = (item) => {
  // 更新购物车项目数量
  if (item.quantity <= 0) {
    removeFromCart(item)
  }
}

const removeFromCart = (item) => {
  const index = cartItems.value.findIndex(cartItem => cartItem.id === item.id)
  if (index > -1) {
    cartItems.value.splice(index, 1)
    ElMessage.success('已从购物车移除')
  }
}

const checkout = () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  
  // 创建批量订单
  createBatchOrders()
}

const createOrder = async () => {
  if (!orderForm.value.deliveryAddress || !orderForm.value.contactPhone) {
    ElMessage.warning('请填写完整的收货信息')
    return
  }

  try {
    const orderData = {
      productId: selectedProduct.value.id,
      buyerId: userInfo.value.id,
      quantity: orderForm.value.quantity,
      unitPrice: selectedProduct.value.price,
      totalAmount: selectedProduct.value.price * orderForm.value.quantity,
      deliveryAddress: orderForm.value.deliveryAddress,
      contactPhone: orderForm.value.contactPhone
    }

    const response = await orderApi.createOrder(orderData)
    if (response.data.code === 200) {
      ElMessage.success('预约成功')
      closeProductDetail()
      loadProducts() // 重新加载产品以更新库存
    } else {
      ElMessage.error(response.data.message || '预约失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('预约失败，请重试')
  }
}

const createBatchOrders = async () => {
  try {
    const orders = []
    
    for (const item of cartItems.value) {
      const orderData = {
        productId: item.id,
        buyerId: userInfo.value.id,
        quantity: item.quantity,
        unitPrice: item.price,
        totalAmount: item.price * item.quantity,
        deliveryAddress: userInfo.value.address || '请填写收货地址',
        contactPhone: userInfo.value.phone || '请填写联系电话'
      }
      orders.push(orderData)
    }
    
    // 批量创建订单
    let successCount = 0
    for (const orderData of orders) {
      try {
        const response = await orderApi.createOrder(orderData)
        if (response.data.code === 200) {
          successCount++
        }
      } catch (error) {
        console.error('创建订单失败:', error)
      }
    }
    
    if (successCount > 0) {
      ElMessage.success(`成功创建 ${successCount} 个订单`)
      cartItems.value = [] // 清空购物车
      showCart.value = false
      loadProducts() // 重新加载产品以更新库存
    } else {
      ElMessage.error('订单创建失败')
    }
  } catch (error) {
    console.error('批量创建订单失败:', error)
    ElMessage.error('订单创建失败，请重试')
  }
}

const viewProductReviews = (productId) => {
  router.push(`/product-reviews/${productId}`)
}

const goToAdvancedSearch = () => {
  router.push('/advanced-search')
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.products-page {
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

.user-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-badge {
  margin-right: 10px;
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

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.search-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-input {
  max-width: 400px;
  position: relative;
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 200px;
  overflow-y: auto;
}

.suggestion-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.suggestion-item:hover {
  background-color: #f5f7fa;
}

.suggestion-item .el-icon {
  margin-right: 8px;
  color: #909399;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.price-slider {
  width: 200px;
}

.price-label {
  font-size: 14px;
  color: #666;
}

.products-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
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
  
  .user-section {
    flex-direction: column;
    gap: 10px;
  }
  
  .search-container {
    gap: 15px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .price-slider {
    width: 100%;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 15px;
  }
  
  .product-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .product-actions .el-button {
    width: 100%;
  }
  
  .search-suggestions {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 400px;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .product-card {
    margin-bottom: 10px;
  }
  
  .main-content {
    padding: 10px;
  }
  
  .search-section {
    padding: 15px;
  }
  
  .products-container {
    padding: 15px;
  }
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
}

.product-info {
  padding: 16px 0;
}

.product-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.product-variety {
  color: #666;
  margin-bottom: 4px;
}

.product-spec {
  color: #999;
  font-size: 14px;
  margin-bottom: 12px;
}

.product-price {
  margin-bottom: 12px;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #E6A23C;
}

.unit {
  font-size: 14px;
  color: #999;
  margin-left: 4px;
}

.product-stock {
  margin-bottom: 16px;
}

.product-actions {
  display: flex;
  gap: 8px;
}

.product-actions .el-button {
  flex: 1;
}

.product-detail {
  display: flex;
  gap: 20px;
}

.detail-image {
  flex: 1;
}

.detail-image img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-info {
  flex: 1;
}

.detail-info h3 {
  font-size: 24px;
  margin-bottom: 16px;
  color: #333;
}

.detail-info p {
  margin-bottom: 12px;
  line-height: 1.6;
}

.order-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.dialog-footer {
  text-align: right;
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
}

.cart-total {
  margin-top: 20px;
  text-align: right;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
}

.cart-total h3 {
  margin: 0;
  color: #E6A23C;
  font-size: 18px;
}
</style>
