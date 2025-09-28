<template>
  <div class="seller-products">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>销售方工作台</h2>
          <p>产品管理</p>
        </div>
        <div class="nav-actions">
          <el-button @click="goToDashboard">返回工作台</el-button>
          <el-button @click="goToOrders">订单管理</el-button>
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

    <el-card class="page-header">
      <div class="header-content">
        <h2>我的产品管理</h2>
        <div class="header-actions">
          <el-button type="success" @click="showStats = true">
            <el-icon><TrendCharts /></el-icon>
            销售统计
          </el-button>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加产品
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 快速统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32" color="#409EFF"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ stats.totalProducts }}</h3>
              <p>总产品数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32" color="#67C23A"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ stats.activeProducts }}</h3>
              <p>上架产品</p>
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
              <h3>{{ stats.totalSales }}</h3>
              <p>总销量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32" color="#F56C6C"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ stats.lowStockProducts }}</h3>
              <p>低库存产品</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索产品名称"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="产品状态" clearable @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="上架中" value="ACTIVE" />
            <el-option label="已下架" value="INACTIVE" />
            <el-option label="售罄" value="OUT_OF_STOCK" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.variety" placeholder="产品品种" clearable @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="鸡" value="鸡" />
            <el-option label="鸭" value="鸭" />
            <el-option label="鹅" value="鹅" />
            <el-option label="猪" value="猪" />
            <el-option label="牛" value="牛" />
            <el-option label="羊" value="羊" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 产品列表 -->
    <el-card class="product-list-card">
      <el-table :data="products" v-loading="loading" stripe>
        <el-table-column prop="name" label="产品名称" min-width="150" />
        <el-table-column prop="variety" label="品种" width="100" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="salesCount" label="销量" width="80" />
        <el-table-column prop="createdAt" label="创建时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editProduct(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 'ACTIVE'" 
              size="small" 
              type="warning" 
              @click="toggleStatus(row, 'INACTIVE')"
            >
              下架
            </el-button>
            <el-button 
              v-else-if="row.status === 'INACTIVE'" 
              size="small" 
              type="success" 
              @click="toggleStatus(row, 'ACTIVE')"
            >
              上架
            </el-button>
            <el-button 
              v-if="row.status === 'ACTIVE' && row.stock <= 0" 
              size="small" 
              type="danger" 
              @click="toggleStatus(row, 'OUT_OF_STOCK')"
            >
              标记售罄
            </el-button>
            <el-button 
              v-if="row.status === 'OUT_OF_STOCK' && row.stock > 0" 
              size="small" 
              type="success" 
              @click="toggleStatus(row, 'ACTIVE')"
            >
              恢复销售
            </el-button>
            <el-button size="small" type="danger" @click="deleteProduct(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑产品对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingProduct ? '编辑产品' : '添加产品'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="productRules"
        label-width="100px"
      >
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="品种" prop="variety">
          <el-select v-model="productForm.variety" placeholder="请选择品种">
            <el-option label="鸡" value="鸡" />
            <el-option label="鸭" value="鸭" />
            <el-option label="鹅" value="鹅" />
            <el-option label="猪" value="猪" />
            <el-option label="牛" value="牛" />
            <el-option label="羊" value="羊" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="productForm.specification" placeholder="如：500g/只" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0"
            :precision="2"
            placeholder="请输入价格"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="productForm.stock"
            :min="0"
            placeholder="请输入库存数量"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="养殖周期" prop="breedingCycle">
          <el-input v-model="productForm.breedingCycle" placeholder="如：45天" />
        </el-form-item>
        <el-form-item label="产品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入产品描述"
          />
        </el-form-item>
        <el-form-item label="产品图片" prop="imageUrl">
          <el-input v-model="productForm.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitProduct">确定</el-button>
      </template>
    </el-dialog>

    <!-- 销售统计对话框 -->
    <el-dialog
      v-model="showStats"
      title="销售统计"
      width="800px"
    >
      <div class="stats-content">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <h4>产品状态分布</h4>
              <div class="status-chart">
                <div class="status-item">
                  <span class="status-dot active"></span>
                  <span>上架中: {{ stats.activeProducts }}</span>
                </div>
                <div class="status-item">
                  <span class="status-dot inactive"></span>
                  <span>已下架: {{ stats.inactiveProducts }}</span>
                </div>
                <div class="status-item">
                  <span class="status-dot out-of-stock"></span>
                  <span>售罄: {{ stats.outOfStockProducts }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <h4>库存预警</h4>
              <div class="stock-warning">
                <div v-if="lowStockProducts.length === 0" class="no-warning">
                  <el-icon size="48" color="#67C23A"><CircleCheck /></el-icon>
                  <p>所有产品库存充足</p>
                </div>
                <div v-else>
                  <div v-for="product in lowStockProducts" :key="product.id" class="warning-item">
                    <span>{{ product.name }}</span>
                    <el-tag type="warning">库存: {{ product.stockQuantity }}</el-tag>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-card style="margin-top: 20px;">
          <h4>最近销售记录</h4>
          <el-table :data="recentSales" size="small">
            <el-table-column prop="productName" label="产品名称" />
            <el-table-column prop="quantity" label="销量" width="80" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="saleTime" label="销售时间" width="150" />
          </el-table>
        </el-card>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, TrendCharts, Box, Money, Warning, CircleCheck } from '@element-plus/icons-vue'
import { productApi } from '../api'
import NotificationCenter from '../components/NotificationCenter.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const showAddDialog = ref(false)
const showStats = ref(false)
const editingProduct = ref(null)
const products = ref([])
const stats = ref({
  totalProducts: 0,
  activeProducts: 0,
  inactiveProducts: 0,
  outOfStockProducts: 0,
  totalSales: 0,
  lowStockProducts: 0
})
const lowStockProducts = ref([])
const recentSales = ref([])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  variety: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 产品表单
const productForm = reactive({
  name: '',
  variety: '',
  specification: '',
  price: 0,
  stock: 0,
  breedingCycle: '',
  description: '',
  imageUrl: ''
})

// 表单验证规则
const productRules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  variety: [{ required: true, message: '请选择品种', trigger: 'change' }],
  specification: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

const productFormRef = ref()

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'ACTIVE': 'success',
    'INACTIVE': 'warning',
    'OUT_OF_STOCK': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'ACTIVE': '上架中',
    'INACTIVE': '已下架',
    'OUT_OF_STOCK': '售罄'
  }
  return statusMap[status] || '未知'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

// 用户信息
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 导航方法
const goToDashboard = () => {
  router.push('/seller-dashboard')
}

const goToOrders = () => {
  router.push('/orders')
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

// 加载产品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.keyword,
      status: searchForm.status,
      variety: searchForm.variety
    }
    
    const response = await productApi.getProducts(params)
    products.value = response.data.records || response.data || []
    pagination.total = response.data.total || 0
    
    // 确保 products.value 是数组后再计算统计
    if (Array.isArray(products.value)) {
      calculateStats()
    } else {
      console.warn('API response is not an array:', response.data)
      products.value = []
      calculateStats()
    }
  } catch (error) {
    console.error('加载产品列表失败:', error)
    ElMessage.error('加载产品列表失败')
    // 使用模拟数据
    products.value = getMockProducts()
    pagination.total = products.value.length
    // 确保 products.value 是数组后再计算统计
    if (Array.isArray(products.value)) {
      calculateStats()
    }
  } finally {
    loading.value = false
  }
}

// 计算统计数据
const calculateStats = () => {
  // 确保 products.value 是数组
  if (!Array.isArray(products.value)) {
    console.warn('products.value is not an array:', products.value)
    products.value = []
  }
  
  const totalProducts = products.value.length
  const activeProducts = products.value.filter(p => p.status === 1).length
  const inactiveProducts = products.value.filter(p => p.status === 0).length
  const outOfStockProducts = products.value.filter(p => p.status === 2).length
  const lowStockProducts = products.value.filter(p => p.stockQuantity <= 5 && p.stockQuantity > 0)
  
  // 计算总销量（模拟数据）
  const totalSales = products.value.reduce((sum, product) => {
    return sum + (product.salesCount || 0)
  }, 0)
  
  stats.value = {
    totalProducts,
    activeProducts,
    inactiveProducts,
    outOfStockProducts,
    totalSales,
    lowStockProducts: lowStockProducts.length
  }
  
  // 设置低库存产品列表
  lowStockProducts.value = lowStockProducts
  
  // 模拟最近销售记录
  recentSales.value = products.value
    .filter(p => p.salesCount > 0)
    .slice(0, 5)
    .map(p => ({
      productName: p.name,
      quantity: p.salesCount || 0,
      totalAmount: (p.price * (p.salesCount || 0)).toFixed(2),
      saleTime: new Date().toLocaleString()
    }))
}

// 模拟数据
const getMockProducts = () => {
  return [
    {
      id: 1,
      name: '优质土鸡',
      variety: '鸡',
      specification: '1.5kg/只',
      price: 68.00,
      stock: 50,
      status: 'ACTIVE',
      salesCount: 120,
      breedingCycle: '120天',
      description: '散养土鸡，肉质鲜美',
      imageUrl: 'https://via.placeholder.com/200x150',
      createdAt: '2024-01-15T10:30:00'
    },
    {
      id: 2,
      name: '生态鸭',
      variety: '鸭',
      specification: '2kg/只',
      price: 45.00,
      stock: 0,
      status: 'OUT_OF_STOCK',
      salesCount: 80,
      breedingCycle: '90天',
      description: '生态养殖，营养丰富',
      imageUrl: 'https://via.placeholder.com/200x150',
      createdAt: '2024-01-10T14:20:00'
    },
    {
      id: 3,
      name: '黑猪',
      variety: '猪',
      specification: '50kg/头',
      price: 2800.00,
      stock: 15,
      status: 'INACTIVE',
      salesCount: 5,
      breedingCycle: '180天',
      description: '纯种黑猪，肉质优良',
      imageUrl: 'https://via.placeholder.com/200x150',
      createdAt: '2024-01-05T09:15:00'
    }
  ]
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadProducts()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  searchForm.variety = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadProducts()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadProducts()
}

// 编辑产品
const editProduct = (product) => {
  editingProduct.value = product
  Object.assign(productForm, {
    name: product.name,
    variety: product.variety,
    specification: product.specification,
    price: product.price,
    stock: product.stock,
    breedingCycle: product.breedingCycle,
    description: product.description,
    imageUrl: product.imageUrl
  })
  showAddDialog.value = true
}

// 切换产品状态
const toggleStatus = async (product, newStatus) => {
  try {
    const action = newStatus === 'ACTIVE' ? '上架' : 
                   newStatus === 'INACTIVE' ? '下架' : '标记售罄'
    
    await ElMessageBox.confirm(
      `确定要${action}产品"${product.name}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用API更新状态
    await productApi.updateProductStatus(product.id, newStatus)
    
    // 更新本地数据
    product.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新产品状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除产品
const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除产品"${product.name}"吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    // 调用API删除产品
    await productApi.deleteProduct(product.id)
    
    // 从列表中移除
    const index = products.value.findIndex(p => p.id === product.id)
    if (index > -1) {
      products.value.splice(index, 1)
    }
    
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除产品失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交产品表单
const submitProduct = async () => {
  try {
    await productFormRef.value.validate()
    
    const productData = { 
      ...productForm,
      sellerId: userInfo.value.id, // 添加销售方ID
      stockQuantity: productForm.stock, // 映射字段名
      breedingCycle: productForm.breedingCycle ? parseInt(productForm.breedingCycle) : null // 转换为数字
    }
    
    if (editingProduct.value) {
      // 编辑产品
      await productApi.updateProduct(editingProduct.value.id, productData)
      ElMessage.success('更新产品成功')
    } else {
      // 添加产品
      await productApi.addProduct(productData)
      ElMessage.success('添加产品成功')
    }
    
    showAddDialog.value = false
    loadProducts()
  } catch (error) {
    console.error('提交产品失败:', error)
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  }
}

// 重置表单
const resetForm = () => {
  editingProduct.value = null
  Object.assign(productForm, {
    name: '',
    variety: '',
    specification: '',
    price: 0,
    stock: 0,
    breedingCycle: '',
    description: '',
    imageUrl: ''
  })
  productFormRef.value?.resetFields()
}

// 组件挂载时加载数据
onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.seller-products {
  padding: 0;
  background-color: #f5f5f5;
  min-height: 100vh;
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

.page-header {
  margin: 0 20px 20px 20px;
}
.seller-products {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.stats-cards {
  margin-bottom: 20px;
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

.header-content h2 {
  margin: 0;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.product-list-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.el-table {
  margin-top: 10px;
}

.el-button + .el-button {
  margin-left: 8px;
}

.stats-content h4 {
  margin: 0 0 16px 0;
  color: #333;
}

.status-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.status-dot.active {
  background-color: #67C23A;
}

.status-dot.inactive {
  background-color: #E6A23C;
}

.status-dot.out-of-stock {
  background-color: #F56C6C;
}

.stock-warning {
  text-align: center;
}

.no-warning {
  padding: 20px;
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
</style>
