<template>
  <div class="admin-page">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>侬帮我养 - 管理后台</h2>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">仪表盘</el-menu-item>
          <el-menu-item index="users">用户管理</el-menu-item>
          <el-menu-item index="products">产品审核</el-menu-item>
          <el-menu-item index="orders">订单管理</el-menu-item>
          <el-menu-item index="statistics">数据统计</el-menu-item>
          <el-menu-item index="settings">系统设置</el-menu-item>
        </el-menu>
        <div class="user-actions">
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
      <div class="admin-container">
        <!-- 仪表盘 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-section">
          <h1>管理仪表盘</h1>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#409EFF"><User /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ statistics.totalUsers }}</h3>
                    <p>总用户数</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#67C23A"><Box /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ statistics.totalProducts }}</h3>
                    <p>总产品数</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#E6A23C"><ShoppingCart /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ statistics.totalOrders }}</h3>
                    <p>总订单数</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#F56C6C"><Money /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>¥{{ statistics.totalRevenue }}</h3>
                    <p>总交易额</p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users-section">
          <div class="section-header">
            <h1>用户管理</h1>
            <div class="header-actions">
              <el-input
                v-model="userSearchKeyword"
                placeholder="搜索用户名或姓名"
                style="width: 200px; margin-right: 10px;"
                @input="searchUsers"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-select v-model="userRoleFilter" placeholder="筛选角色" @change="filterUsers" style="width: 120px; margin-right: 10px;">
                <el-option label="全部" value="" />
                <el-option label="管理员" value="ADMIN" />
                <el-option label="销售方" value="SELLER" />
                <el-option label="购买方" value="BUYER" />
              </el-select>
              <el-button type="primary" @click="showAddUserDialog = true">添加用户</el-button>
            </div>
          </div>
          
          <!-- 用户统计卡片 -->
          <el-row :gutter="20" class="user-stats">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#409EFF"><User /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ userStats.total }}</h3>
                    <p>总用户数</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#67C23A"><Shop /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ userStats.sellers }}</h3>
                    <p>销售方</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#E6A23C"><ShoppingCart /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ userStats.buyers }}</h3>
                    <p>购买方</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#F56C6C"><UserFilled /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ userStats.admins }}</h3>
                    <p>管理员</p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <el-table :data="filteredUsers" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="realName" label="真实姓名" width="120" />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="email" label="邮箱" width="180" />
            <el-table-column prop="role" label="角色" width="100">
              <template #default="scope">
                <el-tag :type="getRoleType(scope.row.role)">
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="注册时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
                <el-button 
                  size="small" 
                  :type="scope.row.status === 1 ? 'danger' : 'success'"
                  @click="toggleUserStatus(scope.row)"
                >
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button size="small" type="danger" @click="deleteUser(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 产品审核 -->
        <div v-if="activeMenu === 'products'" class="products-section">
          <div class="section-header">
            <h1>产品审核</h1>
            <div class="filter-actions">
              <el-select v-model="productFilter" placeholder="筛选状态" @change="loadProducts">
                <el-option label="全部" value="" />
                <el-option label="待审核" value="PENDING" />
                <el-option label="已通过" value="APPROVED" />
                <el-option label="已拒绝" value="REJECTED" />
              </el-select>
              <el-button type="primary" @click="loadProducts">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </div>
          
          <!-- 审核统计 -->
          <el-row :gutter="20" class="audit-stats">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#E6A23C"><Clock /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ auditStats.pending }}</h3>
                    <p>待审核</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#67C23A"><CircleCheck /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ auditStats.approved }}</h3>
                    <p>已通过</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#F56C6C"><Close /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ auditStats.rejected }}</h3>
                    <p>已拒绝</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="24" color="#409EFF"><Box /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ auditStats.total }}</h3>
                    <p>总产品</p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <el-table :data="products" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="产品名称" min-width="150">
              <template #default="scope">
                <div class="product-name">
                  <el-image
                    v-if="scope.row.imageUrl"
                    :src="scope.row.imageUrl"
                    :preview-src-list="[scope.row.imageUrl]"
                    class="product-thumb"
                    fit="cover"
                  />
                  <div class="product-info">
                    <div class="name">{{ scope.row.name }}</div>
                    <div class="description">{{ scope.row.description?.substring(0, 50) }}...</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="variety" label="品种" width="100" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">
                ¥{{ scope.row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="stockQuantity" label="库存" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getProductStatusType(scope.row.status)">
                  {{ getProductStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sellerName" label="销售方" width="120" />
            <el-table-column prop="createdAt" label="提交时间" width="180" />
            <el-table-column prop="auditTime" label="审核时间" width="180">
              <template #default="scope">
                {{ scope.row.auditTime || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="auditorName" label="审核人" width="100">
              <template #default="scope">
                {{ scope.row.auditorName || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250">
              <template #default="scope">
                <el-button size="small" @click="viewProductDetail(scope.row)">查看详情</el-button>
                <el-button 
                  v-if="scope.row.status === 'PENDING'"
                  size="small" 
                  type="success" 
                  @click="showApproveDialog(scope.row)"
                >
                  通过
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'PENDING'"
                  size="small" 
                  type="danger" 
                  @click="showRejectDialog(scope.row)"
                >
                  拒绝
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'APPROVED'"
                  size="small" 
                  type="warning" 
                  @click="showRejectDialog(scope.row)"
                >
                  重新审核
                </el-button>
                <el-button size="small" type="danger" @click="deleteProduct(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 订单管理 -->
        <div v-if="activeMenu === 'orders'" class="orders-section">
          <div class="section-header">
            <h1>订单管理</h1>
            <div class="filter-actions">
              <el-select v-model="orderFilter" placeholder="筛选状态" @change="loadOrders">
                <el-option label="全部" value="" />
                <el-option label="待支付" value="PENDING_PAYMENT" />
                <el-option label="已支付" value="PAID" />
                <el-option label="已发货" value="SHIPPED" />
                <el-option label="已完成" value="COMPLETED" />
              </el-select>
            </div>
          </div>
          
          <el-table :data="orders" v-loading="loading" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="productName" label="产品名称" />
            <el-table-column prop="buyerName" label="购买方" width="120" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="scope">
                ¥{{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getOrderStatusType(scope.row.status)">
                  {{ getOrderStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="orderTime" label="下单时间" width="180" />
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button size="small" @click="viewOrder(scope.row)">查看</el-button>
                <el-button 
                  v-if="scope.row.status === 'PAID'"
                  size="small" 
                  type="primary" 
                  @click="shipOrder(scope.row)"
                >
                  发货
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 数据统计 -->
        <div v-if="activeMenu === 'statistics'" class="statistics-section">
          <div class="section-header">
            <h1>数据统计</h1>
            <el-button type="primary" @click="refreshStatistics">刷新数据</el-button>
          </div>
          
          <el-row :gutter="20" class="stats-grid">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#409EFF"><User /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ statistics.totalUsers }}</h3>
                    <p>总用户数</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#67C23A"><Box /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>{{ statistics.totalProducts }}</h3>
                    <p>总产品数</p>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon">
                  <el-icon size="32" color="#E6A23C"><ShoppingCart /></el-icon>
                </div>
                <div class="stat-info">
                  <h3>{{ statistics.totalOrders }}</h3>
                  <p>总订单数</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon">
                    <el-icon size="32" color="#F56C6C"><Money /></el-icon>
                  </div>
                  <div class="stat-info">
                    <h3>¥{{ statistics.totalRevenue }}</h3>
                    <p>总收入</p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 系统设置 -->
        <div v-if="activeMenu === 'settings'" class="settings-section">
          <h1>系统设置</h1>
          <el-card>
            <h3>系统信息</h3>
            <p>系统名称：侬帮我养在线销售预约系统</p>
            <p>版本：1.0.0</p>
            <p>运行时间：{{ systemUptime }}</p>
          </el-card>
        </div>
      </div>
    </el-main>

    <!-- 添加用户对话框 -->
    <el-dialog v-model="showAddUserDialog" title="添加用户" width="500px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="销售方" value="SELLER" />
            <el-option label="购买方" value="BUYER" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddUserDialog = false">取消</el-button>
          <el-button type="primary" @click="addUser">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 产品详情对话框 -->
    <el-dialog v-model="showProductDetailDialog" title="产品详情" width="800px">
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-image
              :src="selectedProduct.imageUrl || '/default-product.jpg'"
              :alt="selectedProduct.name"
              fit="cover"
              class="product-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>暂无图片</span>
                </div>
              </template>
            </el-image>
          </el-col>
          <el-col :span="12">
            <div class="product-info">
              <h2>{{ selectedProduct.name }}</h2>
              <p class="description">{{ selectedProduct.description }}</p>
              <div class="specs">
                <p><strong>品种：</strong>{{ selectedProduct.variety }}</p>
                <p><strong>价格：</strong>¥{{ selectedProduct.price }}</p>
                <p><strong>库存：</strong>{{ selectedProduct.stockQuantity }}件</p>
                <p><strong>销售方：</strong>{{ selectedProduct.sellerName }}</p>
                <p><strong>提交时间：</strong>{{ selectedProduct.createdAt }}</p>
                <p><strong>状态：</strong>
                  <el-tag :type="getProductStatusType(selectedProduct.status)">
                    {{ getProductStatusText(selectedProduct.status) }}
                  </el-tag>
                </p>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- 审核通过对话框 -->
    <el-dialog v-model="showApproveDialogVisible" title="审核通过" width="500px">
      <el-form :model="auditForm" ref="auditFormRef" label-width="80px">
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showApproveDialogVisible = false">取消</el-button>
          <el-button type="success" @click="approveProduct">确认通过</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 审核拒绝对话框 -->
    <el-dialog v-model="showRejectDialogVisible" title="审核拒绝" width="500px">
      <el-form :model="auditForm" :rules="auditRules" ref="auditFormRef" label-width="80px">
        <el-form-item label="拒绝原因" prop="comment">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请详细说明拒绝原因"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showRejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="rejectProduct">确认拒绝</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Box, ShoppingCart, Money, Search, Shop, UserFilled, Refresh, Clock, CircleCheck, Close, Picture } from '@element-plus/icons-vue'
import { userApi, productApi, orderApi } from '../api'

const router = useRouter()

// 响应式数据
const activeMenu = ref('dashboard')
const loading = ref(false)
const users = ref([])
const products = ref([])
const orders = ref([])
const statistics = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalRevenue: 0
})
const productFilter = ref('')
const orderFilter = ref('')
const showAddUserDialog = ref(false)
const userSearchKeyword = ref('')
const userRoleFilter = ref('')
const userStats = ref({
  total: 0,
  sellers: 0,
  buyers: 0,
  admins: 0
})
const userFormRef = ref()
const auditFormRef = ref()

const userForm = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'BUYER'
})

// 审核相关数据
const showProductDetailDialog = ref(false)
const showApproveDialogVisible = ref(false)
const showRejectDialogVisible = ref(false)
const selectedProduct = ref(null)
const auditForm = ref({
  comment: ''
})
const auditStats = ref({
  pending: 0,
  approved: 0,
  rejected: 0,
  total: 0
})

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const auditRules = {
  comment: [
    { required: true, message: '请输入拒绝原因', trigger: 'blur' }
  ]
}

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 筛选后的用户列表
const filteredUsers = computed(() => {
  let filtered = users.value

  // 按关键词搜索
  if (userSearchKeyword.value) {
    const keyword = userSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(user => 
      user.username.toLowerCase().includes(keyword) ||
      user.realName.toLowerCase().includes(keyword) ||
      user.phone.includes(keyword) ||
      user.email.toLowerCase().includes(keyword)
    )
  }

  // 按角色筛选
  if (userRoleFilter.value) {
    filtered = filtered.filter(user => user.role === userRoleFilter.value)
  }

  return filtered
})

const systemUptime = computed(() => {
  // 简单的系统运行时间计算
  return '1天 2小时 30分钟'
})

// 方法
const handleMenuSelect = (key) => {
  activeMenu.value = key
  if (key === 'users') {
    loadUsers()
  } else if (key === 'products') {
    loadProducts()
  } else if (key === 'orders') {
    loadOrders()
  } else if (key === 'statistics') {
    loadStatistics()
  }
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

const getRoleText = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'SELLER': '销售方',
    'BUYER': '购买方'
  }
  return roleMap[role] || '未知'
}

const getRoleType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'SELLER': 'warning',
    'BUYER': 'success'
  }
  return typeMap[role] || 'info'
}

const loadUsers = async () => {
  try {
    loading.value = true
    const response = await userApi.getAllUsers()
    if (response.data.code === 200) {
      users.value = response.data.data
      calculateUserStats()
    }
  } catch (error) {
    console.error('加载用户失败:', error)
    ElMessage.error('加载用户失败')
    // 使用模拟数据
    users.value = getMockUsers()
    calculateUserStats()
  } finally {
    loading.value = false
  }
}

// 计算用户统计
const calculateUserStats = () => {
  const total = users.value.length
  const sellers = users.value.filter(user => user.role === 'SELLER').length
  const buyers = users.value.filter(user => user.role === 'BUYER').length
  const admins = users.value.filter(user => user.role === 'ADMIN').length
  
  userStats.value = {
    total,
    sellers,
    buyers,
    admins
  }
}

// 搜索用户
const searchUsers = () => {
  // 搜索逻辑在computed中处理
}

// 筛选用户
const filterUsers = () => {
  // 筛选逻辑在computed中处理
}

// 模拟用户数据
const getMockUsers = () => {
  return [
    {
      id: 1,
      username: 'admin',
      realName: '系统管理员',
      phone: '13800138000',
      email: 'admin@example.com',
      role: 'ADMIN',
      status: 1,
      createdAt: '2024-01-01 10:00:00'
    },
    {
      id: 2,
      username: 'seller1',
      realName: '张三农场',
      phone: '13800138001',
      email: 'seller1@example.com',
      role: 'SELLER',
      status: 1,
      createdAt: '2024-01-02 10:00:00'
    },
    {
      id: 3,
      username: 'seller2',
      realName: '李四养殖场',
      phone: '13800138002',
      email: 'seller2@example.com',
      role: 'SELLER',
      status: 1,
      createdAt: '2024-01-03 10:00:00'
    },
    {
      id: 4,
      username: 'buyer1',
      realName: '王五',
      phone: '13800138003',
      email: 'buyer1@example.com',
      role: 'BUYER',
      status: 1,
      createdAt: '2024-01-04 10:00:00'
    },
    {
      id: 5,
      username: 'buyer2',
      realName: '赵六',
      phone: '13800138004',
      email: 'buyer2@example.com',
      role: 'BUYER',
      status: 1,
      createdAt: '2024-01-05 10:00:00'
    }
  ]
}

// 模拟订单数据
const getMockOrders = () => {
  return [
    {
      id: 1,
      orderNo: 'ORD202401010001',
      buyerId: 4,
      sellerId: 2,
      productId: 1,
      quantity: 2,
      unitPrice: 45.00,
      totalAmount: 90.00,
      status: 'PAID',
      paymentMethod: '微信支付',
      paymentTime: '2024-01-01 10:30:00',
      deliveryAddress: '北京市朝阳区建国路88号',
      deliveryPhone: '13800138003',
      deliveryName: '王五',
      orderTime: '2024-01-01 10:00:00',
      productName: '优质土鸡',
      buyerName: '王五'
    },
    {
      id: 2,
      orderNo: 'ORD202401010002',
      buyerId: 5,
      sellerId: 3,
      productId: 3,
      quantity: 5,
      unitPrice: 8.00,
      totalAmount: 40.00,
      status: 'PENDING_PAYMENT',
      paymentMethod: null,
      paymentTime: null,
      deliveryAddress: '上海市浦东新区陆家嘴金融贸易区',
      deliveryPhone: '13800138004',
      deliveryName: '赵六',
      orderTime: '2024-01-01 11:00:00',
      productName: '有机蔬菜',
      buyerName: '赵六'
    },
    {
      id: 3,
      orderNo: 'ORD202401010003',
      buyerId: 4,
      sellerId: 2,
      productId: 2,
      quantity: 10,
      unitPrice: 12.00,
      totalAmount: 120.00,
      status: 'SHIPPED',
      paymentMethod: '支付宝',
      paymentTime: '2024-01-01 12:00:00',
      deliveryAddress: '北京市朝阳区建国路88号',
      deliveryPhone: '13800138003',
      deliveryName: '王五',
      orderTime: '2024-01-01 12:00:00',
      productName: '新鲜鸡蛋',
      buyerName: '王五'
    }
  ]
}

// 模拟产品数据
const getMockProducts = () => {
  return [
    {
      id: 1,
      name: '优质土鸡',
      description: '散养土鸡，肉质鲜美，营养丰富，采用传统养殖方式，无添加激素',
      variety: '土鸡',
      price: 45.00,
      stockQuantity: 100,
      imageUrl: 'https://example.com/chicken1.jpg',
      status: 'APPROVED',
      sellerId: 2,
      sellerName: '张三农场',
      createdAt: '2024-01-02 10:00:00',
      auditTime: '2024-01-02 14:30:00',
      auditorName: '系统管理员'
    },
    {
      id: 2,
      name: '新鲜鸡蛋',
      description: '农场新鲜鸡蛋，营养丰富，每日新鲜收集',
      variety: '鸡蛋',
      price: 12.00,
      stockQuantity: 200,
      imageUrl: 'https://example.com/egg1.jpg',
      status: 'APPROVED',
      sellerId: 2,
      sellerName: '张三农场',
      createdAt: '2024-01-02 10:00:00',
      auditTime: '2024-01-02 14:35:00',
      auditorName: '系统管理员'
    },
    {
      id: 3,
      name: '有机蔬菜',
      description: '无农药有机蔬菜，健康安全，采用有机种植方式',
      variety: '蔬菜',
      price: 8.00,
      stockQuantity: 150,
      imageUrl: 'https://example.com/vegetable1.jpg',
      status: 'PENDING',
      sellerId: 3,
      sellerName: '李四养殖场',
      createdAt: '2024-01-03 10:00:00',
      auditTime: null,
      auditorName: null
    },
    {
      id: 4,
      name: '新鲜牛奶',
      description: '牧场新鲜牛奶，营养丰富，每日现挤',
      variety: '牛奶',
      price: 15.00,
      stockQuantity: 80,
      imageUrl: 'https://example.com/milk1.jpg',
      status: 'REJECTED',
      sellerId: 3,
      sellerName: '李四养殖场',
      createdAt: '2024-01-03 10:00:00',
      auditTime: '2024-01-03 16:20:00',
      auditorName: '系统管理员'
    },
    {
      id: 5,
      name: '生态鸭',
      description: '生态养殖鸭，肉质鲜嫩，无污染',
      variety: '鸭',
      price: 38.00,
      stockQuantity: 60,
      imageUrl: 'https://example.com/duck1.jpg',
      status: 'PENDING',
      sellerId: 2,
      sellerName: '张三农场',
      createdAt: '2024-01-04 09:00:00',
      auditTime: null,
      auditorName: null
    }
  ]
}

const loadStats = async () => {
  try {
    // 加载统计数据
    const usersResponse = await userApi.getAllUsers()
    if (usersResponse.data.code === 200) {
      statistics.value.totalUsers = usersResponse.data.data.length
    }

    const productsResponse = await productApi.getAllProducts()
    if (productsResponse.data.code === 200) {
      statistics.value.totalProducts = productsResponse.data.data.length
    }

    const ordersResponse = await orderApi.getAllOrders()
    if (ordersResponse.data.code === 200) {
      const orders = ordersResponse.data.data
      statistics.value.totalOrders = orders.length
      statistics.value.totalRevenue = orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const addUser = async () => {
  if (!userFormRef.value) return
  
  try {
    const valid = await userFormRef.value.validate()
    if (!valid) return
    
    const response = await userApi.register(userForm.value)
    if (response.data.code === 200) {
      ElMessage.success('用户添加成功')
      showAddUserDialog.value = false
      userForm.value = {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        role: 'BUYER'
      }
      loadUsers()
    } else {
      ElMessage.error(response.data.message || '添加失败')
    }
  } catch (error) {
    console.error('添加用户失败:', error)
    ElMessage.error('添加失败，请重试')
  }
}

const editUser = (user) => {
  // 编辑用户功能
  ElMessage.info('编辑用户功能待实现')
}

const toggleUserStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    const response = await userApi.updateUserStatus(user.id, newStatus)
    if (response.data.code === 200) {
      ElMessage.success('状态更新成功')
      loadUsers()
    } else {
      ElMessage.error(response.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新用户状态失败:', error)
    ElMessage.error('更新失败，请重试')
  }
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm('确认删除此用户吗？', '确认删除', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await userApi.deleteUser(user.id)
    if (response.data.code === 200) {
      ElMessage.success('用户删除成功')
      loadUsers()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const goToProducts = () => {
  router.push('/products')
}

const goToOrders = () => {
  router.push('/orders')
}

// 产品管理相关方法
const viewProduct = (product) => {
  ElMessage.info('查看产品功能待实现')
}

const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认删除此产品吗？', '确认删除', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里调用API删除产品
    ElMessage.success('产品删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除产品失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 订单管理相关方法
const viewOrder = (order) => {
  ElMessage.info('查看订单功能待实现')
}

// 加载产品数据
const loadProducts = async () => {
  try {
    loading.value = true
    const response = await productApi.getAllProducts()
    if (response.data.code === 200) {
      // 获取所有用户信息用于匹配
      const usersResponse = await userApi.getAllUsers()
      const usersMap = {}
      if (usersResponse.data.code === 200) {
        usersResponse.data.data.forEach(user => {
          usersMap[user.id] = user.realName || user.username
        })
      }
      
      products.value = response.data.data.map(product => ({
        ...product,
        sellerName: usersMap[product.sellerId] || '未知销售方',
        auditTime: product.auditTime || null,
        auditorName: product.auditorName || null
      }))
      
      // 计算审核统计
      calculateAuditStats()
    }
  } catch (error) {
    console.error('加载产品失败:', error)
    ElMessage.error('加载产品失败')
    // 使用模拟数据
    products.value = getMockProducts()
    calculateAuditStats()
  } finally {
    loading.value = false
  }
}

// 计算审核统计
const calculateAuditStats = () => {
  const total = products.value.length
  const pending = products.value.filter(p => p.status === 'PENDING' || p.status === 2).length
  const approved = products.value.filter(p => p.status === 'APPROVED' || p.status === 1).length
  const rejected = products.value.filter(p => p.status === 'REJECTED' || p.status === 3).length
  
  auditStats.value = {
    total,
    pending,
    approved,
    rejected
  }
}

// 加载订单数据
const loadOrders = async () => {
  try {
    loading.value = true
    const response = await orderApi.getAllOrders()
    if (response.data.code === 200) {
      // 获取所有用户信息用于匹配
      const usersResponse = await userApi.getAllUsers()
      const usersMap = {}
      if (usersResponse.data.code === 200) {
        usersResponse.data.data.forEach(user => {
          usersMap[user.id] = user.realName || user.username
        })
      }
      
      // 获取所有产品信息用于匹配
      const productsResponse = await productApi.getAllProducts()
      const productsMap = {}
      if (productsResponse.data.code === 200) {
        productsResponse.data.data.forEach(product => {
          productsMap[product.id] = product.name
        })
      }
      
      orders.value = response.data.data.map(order => ({
        ...order,
        productName: productsMap[order.productId] || '未知产品',
        buyerName: usersMap[order.buyerId] || '未知用户'
      }))
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
    // 使用模拟数据
    orders.value = getMockOrders()
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 这里可以调用统计API
    statistics.value = {
      totalUsers: users.value.length,
      totalProducts: products.value.length,
      totalOrders: orders.value.length,
      totalRevenue: orders.value.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 刷新统计数据
const refreshStatistics = () => {
  loadStatistics()
  ElMessage.success('数据已刷新')
}

// 产品审核相关方法
const viewProductDetail = (product) => {
  selectedProduct.value = product
  showProductDetailDialog.value = true
}

const showApproveDialog = (product) => {
  selectedProduct.value = product
  auditForm.value.comment = ''
  showApproveDialogVisible.value = true
}

const showRejectDialog = (product) => {
  selectedProduct.value = product
  auditForm.value.comment = ''
  showRejectDialogVisible.value = true
}

const approveProduct = async () => {
  try {
    if (!auditFormRef.value) return
    
    const valid = await auditFormRef.value.validate()
    if (!valid) return
    
    // 调用API审核通过产品
    const response = await productApi.updateProductStatus(selectedProduct.value.id, 'APPROVED')
    if (response.data.code === 200) {
      ElMessage.success('产品审核通过')
      showApproveDialogVisible.value = false
      loadProducts()
      
      // 发送通知给销售方
      await sendAuditNotification(selectedProduct.value.sellerId, 'APPROVED', selectedProduct.value.name, auditForm.value.comment)
    } else {
      ElMessage.error(response.data.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

const rejectProduct = async () => {
  try {
    if (!auditFormRef.value) return
    
    const valid = await auditFormRef.value.validate()
    if (!valid) return
    
    // 调用API审核拒绝产品
    const response = await productApi.updateProductStatus(selectedProduct.value.id, 'REJECTED')
    if (response.data.code === 200) {
      ElMessage.success('产品审核拒绝')
      showRejectDialogVisible.value = false
      loadProducts()
      
      // 发送通知给销售方
      await sendAuditNotification(selectedProduct.value.sellerId, 'REJECTED', selectedProduct.value.name, auditForm.value.comment)
    } else {
      ElMessage.error(response.data.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

// 发送审核通知
const sendAuditNotification = async (sellerId, status, productName, comment) => {
  try {
    const notificationData = {
      userId: sellerId,
      type: 'PRODUCT_AUDIT',
      title: status === 'APPROVED' ? '产品审核通过' : '产品审核拒绝',
      content: status === 'APPROVED' 
        ? `您的产品"${productName}"已通过审核，可以正常销售了。${comment ? '审核意见：' + comment : ''}`
        : `您的产品"${productName}"审核未通过。拒绝原因：${comment}`,
      relatedId: selectedProduct.value.id
    }
    
    // 这里调用通知API
    console.log('发送审核通知:', notificationData)
  } catch (error) {
    console.error('发送通知失败:', error)
  }
}

// 订单管理相关方法
const shipOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认发货此订单吗？', '确认发货', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里调用API更新订单状态
    order.status = 'SHIPPED'
    ElMessage.success('订单已发货')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发货失败:', error)
      ElMessage.error('发货失败')
    }
  }
}

// 状态文本转换方法
const getProductStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'ACTIVE': '上架中',
    'INACTIVE': '已下架',
    'OUT_OF_STOCK': '售罄'
  }
  return statusMap[status] || status
}

const getProductStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'ACTIVE': 'success',
    'INACTIVE': 'warning',
    'OUT_OF_STOCK': 'danger'
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
  loadUsers()
  loadStats()
})
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 0;
}

.nav-container {
  max-width: 1400px;
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

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.admin-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.dashboard-section h1,
.users-section h1,
.products-section h1,
.orders-section h1,
.settings-section h1 {
  color: #333;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.filter-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.stats-grid {
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}

/* 审核相关样式 */
.audit-stats {
  margin-bottom: 20px;
}

.product-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-thumb {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.product-info .name {
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.product-info .description {
  color: #666;
  font-size: 12px;
  line-height: 1.4;
}

.product-detail {
  padding: 20px 0;
}

.product-image {
  width: 100%;
  height: 300px;
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

.product-info h2 {
  margin: 0 0 15px 0;
  color: #333;
}

.product-info .description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.specs p {
  margin: 8px 0;
  color: #333;
}

.specs strong {
  color: #666;
  margin-right: 8px;
}
</style>
