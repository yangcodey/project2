<template>
  <div class="marketplace">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>销售方商城</h2>
          <p>采购优质产品，丰富您的库存</p>
        </div>
        <div class="nav-actions">
          <el-button @click="goToDashboard">返回工作台</el-button>
          <el-button @click="goToInventory">我的库存</el-button>
          <el-button @click="goToOrders">采购订单</el-button>
        </div>
        <div class="user-actions">
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
      <!-- 搜索和筛选 -->
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="关键词">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索产品名称"
              style="width: 200px;"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch" :icon="Search">搜索</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="品种">
            <el-select v-model="searchForm.variety" placeholder="选择品种" @change="handleSearch">
              <el-option label="全部" value="" />
              <el-option label="鸡" value="鸡" />
              <el-option label="鸭" value="鸭" />
              <el-option label="鹅" value="鹅" />
              <el-option label="猪" value="猪" />
              <el-option label="牛" value="牛" />
              <el-option label="羊" value="羊" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="searchForm.categoryId" placeholder="选择分类" @change="handleSearch">
              <el-option label="全部" value="" />
              <el-option label="家禽类" :value="1" />
              <el-option label="家畜类" :value="2" />
              <el-option label="蛋类" :value="3" />
              <el-option label="水产类" :value="4" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 产品列表 -->
      <el-card class="products-card">
        <template #header>
          <div class="card-header">
            <h3>可采购产品</h3>
            <el-button @click="refreshProducts" :icon="Refresh">刷新</el-button>
          </div>
        </template>

        <div v-loading="loading" class="products-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="viewProductDetail(product)"
          >
            <div class="product-image">
              <img :src="product.image_url || '/default-product.jpg'" :alt="product.title" />
              <div class="product-badges">
                <el-tag v-if="product.is_featured" type="warning" size="small">推荐</el-tag>
                <el-tag v-if="product.is_hot" type="danger" size="small">热门</el-tag>
              </div>
            </div>
            <div class="product-info">
              <h4 class="product-title">{{ product.title }}</h4>
              <p class="product-seller">供应商: {{ product.seller_name }}</p>
              <p class="product-spec">{{ product.specification }}</p>
              <div class="product-price">
                <span class="price">¥{{ product.selling_price }}</span>
                <span class="unit">/件</span>
              </div>
              <div class="product-stock">
                <el-tag :type="product.available_quantity > 50 ? 'success' : product.available_quantity > 10 ? 'warning' : 'danger'">
                  库存: {{ product.available_quantity }}
                </el-tag>
              </div>
              <div class="product-supplier-info">
                <p>供应商价格: ¥{{ product.supplier_price }}</p>
                <p>最小订购: {{ product.min_order_quantity }}件</p>
                <p>交货周期: {{ product.lead_time }}天</p>
              </div>
              <div class="product-actions">
                <el-button type="primary" @click.stop="showPurchaseDialog(product)">
                  <el-icon><ShoppingCart /></el-icon>
                  立即采购
                </el-button>
                <el-button @click.stop="viewProductDetail(product)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && products.length === 0" description="暂无可采购的产品" />
      </el-card>
    </el-main>

    <!-- 产品详情对话框 -->
    <el-dialog
      v-model="productDetailVisible"
      :title="selectedProduct?.title"
      width="800px"
      @close="closeProductDetail"
    >
      <div v-if="selectedProduct" class="product-detail">
        <div class="detail-image">
          <img :src="selectedProduct.image_url || '/default-product.jpg'" :alt="selectedProduct.title" />
        </div>
        <div class="detail-info">
          <h3>{{ selectedProduct.title }}</h3>
          <p><strong>供应商:</strong> {{ selectedProduct.seller_name }}</p>
          <p><strong>品种:</strong> {{ selectedProduct.variety }}</p>
          <p><strong>规格:</strong> {{ selectedProduct.specification }}</p>
          <p><strong>销售价格:</strong> ¥{{ selectedProduct.selling_price }}</p>
          <p><strong>供应商价格:</strong> ¥{{ selectedProduct.supplier_price }}</p>
          <p><strong>库存:</strong> {{ selectedProduct.available_quantity }}</p>
          <p><strong>最小订购量:</strong> {{ selectedProduct.min_order_quantity }}件</p>
          <p><strong>最大订购量:</strong> {{ selectedProduct.max_order_quantity }}件</p>
          <p><strong>交货周期:</strong> {{ selectedProduct.lead_time }}天</p>
          <p><strong>描述:</strong> {{ selectedProduct.description }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="closeProductDetail">关闭</el-button>
        <el-button type="primary" @click="showPurchaseDialog(selectedProduct)">立即采购</el-button>
      </template>
    </el-dialog>

    <!-- 采购对话框 -->
    <el-dialog
      v-model="purchaseDialogVisible"
      title="采购产品"
      width="600px"
      @close="closePurchaseDialog"
    >
      <el-form :model="purchaseForm" :rules="purchaseRules" ref="purchaseFormRef" label-width="100px">
        <el-form-item label="产品名称">
          <el-input v-model="purchaseForm.productName" disabled />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="purchaseForm.supplierName" disabled />
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model="purchaseForm.unitPrice" disabled />
        </el-form-item>
        <el-form-item label="采购数量" prop="quantity">
          <el-input-number
            v-model="purchaseForm.quantity"
            :min="purchaseForm.minQuantity"
            :max="purchaseForm.maxQuantity"
            controls-position="right"
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="总金额">
          <el-input v-model="purchaseForm.totalAmount" disabled />
        </el-form-item>
        <el-form-item label="收货地址" prop="deliveryAddress">
          <el-input
            v-model="purchaseForm.deliveryAddress"
            type="textarea"
            placeholder="请输入收货地址"
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input
            v-model="purchaseForm.contactPhone"
            placeholder="请输入联系电话"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="purchaseForm.notes"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closePurchaseDialog">取消</el-button>
        <el-button type="primary" @click="submitPurchase" :loading="purchaseLoading">确认采购</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, ShoppingCart, View } from '@element-plus/icons-vue'
import { marketplaceApi } from '../api'
import NotificationCenter from '../components/NotificationCenter.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const products = ref([])
const searchForm = ref({
  keyword: '',
  variety: '',
  categoryId: ''
})
const productDetailVisible = ref(false)
const selectedProduct = ref(null)
const purchaseDialogVisible = ref(false)
const purchaseLoading = ref(false)
const purchaseFormRef = ref()

const purchaseForm = ref({
  productId: null,
  supplierId: null,
  productName: '',
  supplierName: '',
  unitPrice: 0,
  quantity: 1,
  minQuantity: 1,
  maxQuantity: 9999,
  totalAmount: 0,
  deliveryAddress: '',
  contactPhone: '',
  notes: ''
})

// 表单验证规则
const purchaseRules = {
  quantity: [
    { required: true, message: '请输入采购数量', trigger: 'blur' }
  ],
  deliveryAddress: [
    { required: true, message: '请输入收货地址', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ]
}

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const loadProducts = async () => {
  loading.value = true
  try {
    const response = await marketplaceApi.getMarketplaceProducts({
      keyword: searchForm.value.keyword,
      categoryId: searchForm.value.categoryId,
      variety: searchForm.value.variety
    })
    if (response.data.code === 200) {
      products.value = response.data.data
    } else {
      ElMessage.error('加载产品失败')
    }
  } catch (error) {
    console.error('加载产品失败:', error)
    ElMessage.error('加载产品失败')
    // 使用模拟数据
    products.value = getMockProducts()
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadProducts()
}

const refreshProducts = () => {
  loadProducts()
}

const viewProductDetail = (product) => {
  selectedProduct.value = product
  productDetailVisible.value = true
}

const closeProductDetail = () => {
  productDetailVisible.value = false
  selectedProduct.value = null
}

const showPurchaseDialog = (product) => {
  if (!product) return
  
  purchaseForm.value = {
    productId: product.product_id,
    supplierId: product.seller_id,
    productName: product.title,
    supplierName: product.seller_name,
    unitPrice: product.supplier_price,
    quantity: product.min_order_quantity,
    minQuantity: product.min_order_quantity,
    maxQuantity: product.max_order_quantity,
    totalAmount: product.supplier_price * product.min_order_quantity,
    deliveryAddress: '',
    contactPhone: '',
    notes: ''
  }
  
  purchaseDialogVisible.value = true
}

const closePurchaseDialog = () => {
  purchaseDialogVisible.value = false
  purchaseForm.value = {
    productId: null,
    supplierId: null,
    productName: '',
    supplierName: '',
    unitPrice: 0,
    quantity: 1,
    minQuantity: 1,
    maxQuantity: 9999,
    totalAmount: 0,
    deliveryAddress: '',
    contactPhone: '',
    notes: ''
  }
}

const submitPurchase = async () => {
  try {
    await purchaseFormRef.value.validate()
    
    purchaseLoading.value = true
    
    const orderData = {
      productId: purchaseForm.value.productId,
      supplierId: purchaseForm.value.supplierId,
      quantity: purchaseForm.value.quantity,
      unitPrice: purchaseForm.value.unitPrice,
      totalAmount: purchaseForm.value.totalAmount,
      deliveryAddress: purchaseForm.value.deliveryAddress,
      contactPhone: purchaseForm.value.contactPhone,
      notes: purchaseForm.value.notes
    }
    
    const response = await marketplaceApi.createPurchaseOrder(orderData)
    if (response.data.code === 200) {
      ElMessage.success('采购订单创建成功')
      closePurchaseDialog()
      loadProducts()
    } else {
      ElMessage.error('采购订单创建失败')
    }
  } catch (error) {
    console.error('创建采购订单失败:', error)
    ElMessage.error('创建采购订单失败')
  } finally {
    purchaseLoading.value = false
  }
}

const goToDashboard = () => {
  router.push('/seller-dashboard')
}

const goToInventory = () => {
  ElMessage.info('库存管理功能开发中')
}

const goToOrders = () => {
  ElMessage.info('采购订单管理功能开发中')
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

// 模拟数据
const getMockProducts = () => {
  return [
    {
      id: 1,
      product_id: 1,
      seller_id: 2,
      title: '【张三农场】散养土鸡 2.5kg/只',
      seller_name: '张三农场',
      variety: '鸡',
      specification: '2.5kg/只',
      image_url: 'https://via.placeholder.com/200x150',
      selling_price: 68.00,
      supplier_price: 45.00,
      available_quantity: 200,
      min_order_quantity: 10,
      max_order_quantity: 1000,
      lead_time: 3,
      is_featured: true,
      is_hot: true
    },
    {
      id: 2,
      product_id: 2,
      seller_id: 3,
      title: '【李四养殖场】生态鸡蛋 30枚/盒',
      seller_name: '李四养殖场',
      variety: '蛋',
      specification: '30枚/盒',
      image_url: 'https://via.placeholder.com/200x150',
      selling_price: 45.00,
      supplier_price: 38.00,
      available_quantity: 300,
      min_order_quantity: 10,
      max_order_quantity: 1000,
      lead_time: 3,
      is_featured: true,
      is_hot: false
    }
  ]
}

// 监听数量变化，自动计算总金额
const updateTotalAmount = () => {
  purchaseForm.value.totalAmount = (purchaseForm.value.unitPrice * purchaseForm.value.quantity).toFixed(2)
}

// 监听数量变化
watch(() => purchaseForm.value.quantity, updateTotalAmount)

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.marketplace {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 0;
  margin-bottom: 20px;
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

.nav-actions {
  display: flex;
  gap: 10px;
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
  padding: 0 20px;
}

.search-card {
  margin-bottom: 20px;
}

.products-card {
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

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
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
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-badges {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.product-info {
  padding: 16px;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #333;
  line-height: 1.4;
}

.product-seller {
  color: #666;
  font-size: 14px;
  margin: 0 0 4px 0;
}

.product-spec {
  color: #999;
  font-size: 12px;
  margin: 0 0 12px 0;
}

.product-price {
  margin-bottom: 8px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #E6A23C;
}

.unit {
  font-size: 14px;
  color: #999;
  margin-left: 4px;
}

.product-stock {
  margin-bottom: 8px;
}

.product-supplier-info {
  background: #f8f9fa;
  padding: 8px;
  border-radius: 4px;
  margin-bottom: 12px;
}

.product-supplier-info p {
  margin: 0 0 4px 0;
  font-size: 12px;
  color: #666;
}

.product-supplier-info p:last-child {
  margin-bottom: 0;
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
  font-size: 20px;
  margin-bottom: 16px;
  color: #333;
}

.detail-info p {
  margin-bottom: 12px;
  line-height: 1.6;
}
</style>
