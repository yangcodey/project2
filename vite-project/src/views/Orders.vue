<template>
  <div class="orders-page">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>侬帮我养</h2>
          <p>在线销售预约系统</p>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="'orders'"
          class="nav-menu"
          @select="handleSelect"
        >
          <el-menu-item index="home">首页</el-menu-item>
          <el-menu-item index="products">产品展示</el-menu-item>
          <el-menu-item index="orders">订单管理</el-menu-item>
          <el-menu-item index="profile">个人资料</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.realName?.charAt(0) }}</el-avatar>
              <span class="username">{{ userInfo.realName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="dashboard">个人中心</el-dropdown-item>
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
      <div class="orders-container">
        <div class="page-header">
          <h1>订单管理</h1>
          <p>管理您的所有订单</p>
        </div>

        <!-- 筛选条件 -->
        <div class="filter-section">
          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="订单状态">
              <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
                <el-option label="全部" value="" />
                <el-option label="待支付" value="PENDING_PAYMENT" />
                <el-option label="已支付" value="PAID" />
                <el-option label="已取消" value="CANCELLED" />
                <el-option label="已发货" value="SHIPPED" />
                <el-option label="已送达" value="DELIVERED" />
                <el-option label="已完成" value="COMPLETED" />
              </el-select>
            </el-form-item>
            <el-form-item label="订单号">
              <el-input v-model="filterForm.orderNo" placeholder="输入订单号" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleFilter">筛选</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 订单列表 -->
        <div class="orders-list">
          <el-table :data="filteredOrders" v-loading="loading" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="productName" label="产品名称" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="unitPrice" label="单价" width="100">
              <template #default="scope">
                ¥{{ scope.row.unitPrice }}
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="总金额" width="120">
              <template #default="scope">
                ¥{{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="orderTime" label="下单时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="viewOrder(scope.row)">查看</el-button>
                <el-button 
                  v-if="scope.row.status === 'PENDING_PAYMENT'" 
                  size="small" 
                  type="primary" 
                  @click="payOrder(scope.row)"
                >
                  支付
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'PENDING_PAYMENT'" 
                  size="small" 
                  type="danger" 
                  @click="cancelOrder(scope.row)"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalOrders"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-main>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailVisible"
      :title="'订单详情 - ' + selectedOrder?.orderNo"
      width="800px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(selectedOrder.status)">
              {{ getStatusText(selectedOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="产品名称">{{ selectedOrder.productName }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ selectedOrder.quantity }}</el-descriptions-item>
          <el-descriptions-item label="单价">¥{{ selectedOrder.unitPrice }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ selectedOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ selectedOrder.orderTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ selectedOrder.paymentTime || '未支付' }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ selectedOrder.deliveryAddress }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedOrder.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ selectedOrder.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="orderDetailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '../api'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const orders = ref([])
const filterForm = ref({
  status: '',
  orderNo: ''
})
const currentPage = ref(1)
const pageSize = ref(10)
const orderDetailVisible = ref(false)
const selectedOrder = ref(null)

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

const filteredOrders = computed(() => {
  let filtered = orders.value

  if (filterForm.value.status) {
    filtered = filtered.filter(order => order.status === filterForm.value.status)
  }

  if (filterForm.value.orderNo) {
    filtered = filtered.filter(order => 
      order.orderNo.toLowerCase().includes(filterForm.value.orderNo.toLowerCase())
    )
  }

  return filtered
})

const totalOrders = computed(() => filteredOrders.value.length)

// 方法
const handleSelect = (key) => {
  switch (key) {
    case 'home':
      router.push('/')
      break
    case 'products':
      router.push('/products')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'profile':
      router.push('/profile')
      break
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'dashboard':
      router.push('/dashboard')
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

const getStatusText = (status) => {
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

const getStatusType = (status) => {
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

const handleFilter = () => {
  // 筛选逻辑在computed中处理
}

const resetFilter = () => {
  filterForm.value = {
    status: '',
    orderNo: ''
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const loadOrders = async () => {
  try {
    loading.value = true
    const response = await orderApi.getAllOrders()
    if (response.data.code === 200) {
      orders.value = response.data.data.map(order => ({
        ...order,
        productName: '产品名称' // 这里应该从产品信息中获取
      }))
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const viewOrder = (order) => {
  selectedOrder.value = order
  orderDetailVisible.value = true
}

const payOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认支付此订单吗？', '确认支付', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await orderApi.payOrder(order.id, '在线支付')
    if (response.data.code === 200) {
      ElMessage.success('支付成功')
      loadOrders()
    } else {
      ElMessage.error(response.data.message || '支付失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  }
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认取消此订单吗？', '确认取消', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await orderApi.cancelOrder(order.id)
    if (response.data.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    } else {
      ElMessage.error(response.data.message || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
      ElMessage.error('取消失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
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

.orders-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-header h1 {
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.filter-form {
  margin: 0;
}

.orders-list {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}

.order-detail {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>
