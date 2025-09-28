<template>
  <div class="feature-test">
    <el-card class="page-header">
      <h2>功能测试页面</h2>
      <p>测试上架、下架、购买等功能</p>
    </el-card>

    <!-- 用户角色切换 -->
    <el-card class="role-switch">
      <h3>切换用户角色</h3>
      <el-radio-group v-model="currentRole" @change="switchRole">
        <el-radio label="BUYER">购买方</el-radio>
        <el-radio label="SELLER">销售方</el-radio>
        <el-radio label="ADMIN">管理员</el-radio>
      </el-radio-group>
    </el-card>

    <!-- 功能测试区域 -->
    <el-row :gutter="20">
      <!-- 产品管理测试 -->
      <el-col :span="12">
        <el-card class="test-card">
          <template #header>
            <h3>产品管理测试</h3>
          </template>
          
          <div class="test-section">
            <h4>1. 添加产品</h4>
            <el-form :model="productForm" label-width="80px">
              <el-form-item label="产品名称">
                <el-input v-model="productForm.name" placeholder="输入产品名称" />
              </el-form-item>
              <el-form-item label="品种">
                <el-select v-model="productForm.variety" placeholder="选择品种">
                  <el-option label="鸡" value="鸡" />
                  <el-option label="鸭" value="鸭" />
                  <el-option label="猪" value="猪" />
                </el-select>
              </el-form-item>
              <el-form-item label="价格">
                <el-input-number v-model="productForm.price" :min="0" :precision="2" />
              </el-form-item>
              <el-form-item label="库存">
                <el-input-number v-model="productForm.stock" :min="0" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="addProduct">添加产品</el-button>
              </el-form-item>
            </el-form>
          </div>

          <div class="test-section">
            <h4>2. 产品状态管理</h4>
            <el-table :data="products" size="small">
              <el-table-column prop="name" label="产品名称" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button size="small" @click="toggleProductStatus(row)">
                    {{ row.status === 1 ? '下架' : '上架' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

      <!-- 购买流程测试 -->
      <el-col :span="12">
        <el-card class="test-card">
          <template #header>
            <h3>购买流程测试</h3>
          </template>
          
          <div class="test-section">
            <h4>1. 产品列表</h4>
            <el-table :data="availableProducts" size="small">
              <el-table-column prop="name" label="产品名称" />
              <el-table-column prop="price" label="价格" />
              <el-table-column prop="stockQuantity" label="库存" />
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button size="small" type="primary" @click="buyProduct(row)">
                    购买
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="test-section">
            <h4>2. 订单管理</h4>
            <el-table :data="orders" size="small">
              <el-table-column prop="orderNo" label="订单号" />
              <el-table-column prop="totalAmount" label="金额" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getOrderStatusType(row.status)">
                    {{ getOrderStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button 
                    v-if="row.status === 'PENDING_PAYMENT'" 
                    size="small" 
                    type="success" 
                    @click="payOrder(row)"
                  >
                    支付
                  </el-button>
                  <el-button 
                    v-if="row.status === 'PENDING_PAYMENT'" 
                    size="small" 
                    type="danger" 
                    @click="cancelOrder(row)"
                  >
                    取消
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 测试结果 -->
    <el-card class="test-results">
      <h3>测试结果</h3>
      <el-alert
        v-for="(result, index) in testResults"
        :key="index"
        :title="result.title"
        :type="result.type"
        :description="result.description"
        show-icon
        :closable="false"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { productApi, orderApi } from '../api'

// 响应式数据
const currentRole = ref('BUYER')
const products = ref([])
const orders = ref([])
const testResults = ref([])

// 产品表单
const productForm = ref({
  name: '',
  variety: '',
  price: 0,
  stock: 0,
  specification: '测试规格',
  description: '测试产品描述',
  imageUrl: 'https://via.placeholder.com/200x150'
})

// 计算属性
const availableProducts = computed(() => {
  return products.value.filter(p => p.status === 1 && p.stockQuantity > 0)
})

// 方法
const switchRole = (role) => {
  // 模拟切换用户角色
  const mockUser = {
    id: 1,
    username: 'testuser',
    realName: '测试用户',
    role: role,
    phone: '13800138000',
    address: '测试地址'
  }
  localStorage.setItem('user', JSON.stringify(mockUser))
  localStorage.setItem('token', 'mock-token')
  
  addTestResult('角色切换', 'success', `已切换到${role}角色`)
  loadData()
}

const addTestResult = (title, type, description) => {
  testResults.value.unshift({
    title,
    type,
    description,
    timestamp: new Date().toLocaleTimeString()
  })
  
  // 只保留最近10条结果
  if (testResults.value.length > 10) {
    testResults.value = testResults.value.slice(0, 10)
  }
}

const loadData = async () => {
  try {
    // 加载产品
    const productsResponse = await productApi.getAllProducts()
    if (productsResponse.data.code === 200) {
      products.value = productsResponse.data.data
    } else {
      // 使用模拟数据
      products.value = getMockProducts()
    }
    
    // 加载订单
    const ordersResponse = await orderApi.getAllOrders()
    if (ordersResponse.data.code === 200) {
      orders.value = ordersResponse.data.data
    } else {
      // 使用模拟数据
      orders.value = getMockOrders()
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    // 使用模拟数据
    products.value = getMockProducts()
    orders.value = getMockOrders()
  }
}

const getMockProducts = () => {
  return [
    {
      id: 1,
      name: '测试土鸡',
      variety: '鸡',
      price: 68.00,
      stockQuantity: 10,
      status: 1,
      specification: '1.5kg/只',
      description: '测试产品'
    },
    {
      id: 2,
      name: '测试鸭子',
      variety: '鸭',
      price: 45.00,
      stockQuantity: 0,
      status: 2,
      specification: '2kg/只',
      description: '测试产品'
    }
  ]
}

const getMockOrders = () => {
  return [
    {
      id: 1,
      orderNo: 'NBWY20240116001',
      productId: 1,
      quantity: 2,
      totalAmount: 136.00,
      status: 'PENDING_PAYMENT',
      orderTime: '2024-01-16 10:30:00'
    },
    {
      id: 2,
      orderNo: 'NBWY20240116002',
      productId: 2,
      quantity: 1,
      totalAmount: 45.00,
      status: 'PAID',
      orderTime: '2024-01-16 11:00:00'
    }
  ]
}

const addProduct = async () => {
  try {
    const productData = {
      ...productForm.value,
      sellerId: 1,
      status: 1
    }
    
    const response = await productApi.addProduct(productData)
    if (response.data.code === 200) {
      ElMessage.success('产品添加成功')
      addTestResult('添加产品', 'success', `产品"${productForm.value.name}"添加成功`)
      loadData()
    } else {
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('添加产品失败:', error)
    ElMessage.error('添加产品失败')
    addTestResult('添加产品', 'error', `添加失败: ${error.message}`)
  }
}

const toggleProductStatus = async (product) => {
  try {
    const newStatus = product.status === 1 ? 'INACTIVE' : 'ACTIVE'
    const response = await productApi.updateProductStatus(product.id, newStatus)
    
    if (response.data.code === 200) {
      ElMessage.success('状态更新成功')
      addTestResult('状态更新', 'success', `产品"${product.name}"状态已更新`)
      loadData()
    } else {
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
    addTestResult('状态更新', 'error', `更新失败: ${error.message}`)
  }
}

const buyProduct = async (product) => {
  try {
    const orderData = {
      productId: product.id,
      buyerId: 1,
      quantity: 1,
      unitPrice: product.price,
      totalAmount: product.price,
      deliveryAddress: '测试地址',
      contactPhone: '13800138000'
    }
    
    const response = await orderApi.createOrder(orderData)
    if (response.data.code === 200) {
      ElMessage.success('购买成功')
      addTestResult('购买产品', 'success', `成功购买"${product.name}"`)
      loadData()
    } else {
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('购买失败:', error)
    ElMessage.error('购买失败')
    addTestResult('购买产品', 'error', `购买失败: ${error.message}`)
  }
}

const payOrder = async (order) => {
  try {
    const response = await orderApi.payOrder(order.id, '在线支付')
    if (response.data.code === 200) {
      ElMessage.success('支付成功')
      addTestResult('支付订单', 'success', `订单${order.orderNo}支付成功`)
      loadData()
    } else {
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
    addTestResult('支付订单', 'error', `支付失败: ${error.message}`)
  }
}

const cancelOrder = async (order) => {
  try {
    const response = await orderApi.cancelOrder(order.id)
    if (response.data.code === 200) {
      ElMessage.success('订单已取消')
      addTestResult('取消订单', 'success', `订单${order.orderNo}已取消`)
      loadData()
    } else {
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('取消失败:', error)
    ElMessage.error('取消失败')
    addTestResult('取消订单', 'error', `取消失败: ${error.message}`)
  }
}

const getStatusText = (status) => {
  const statusMap = {
    1: '上架中',
    0: '已下架',
    2: '售罄'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    1: 'success',
    0: 'warning',
    2: 'danger'
  }
  return typeMap[status] || 'info'
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
  loadData()
})
</script>

<style scoped>
.feature-test {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.role-switch {
  margin-bottom: 20px;
}

.test-card {
  margin-bottom: 20px;
}

.test-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.test-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.test-results {
  margin-top: 20px;
}

.test-results .el-alert {
  margin-bottom: 10px;
}
</style>
