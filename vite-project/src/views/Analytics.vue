<template>
  <div class="analytics">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1>数据分析</h1>
          <p>系统运营数据统计与分析</p>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <!-- 概览统计 -->
        <el-card class="overview-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统概览</span>
              <el-button type="primary" @click="refreshOverview">刷新数据</el-button>
            </div>
          </template>
          <div class="overview-grid" v-loading="overviewLoading">
            <div class="stat-item">
              <div class="stat-icon users">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ overviewData.users?.total || 0 }}</h3>
                <p>总用户数</p>
                <div class="stat-details">
                  <span>管理员: {{ overviewData.users?.admin || 0 }}</span>
                  <span>销售方: {{ overviewData.users?.seller || 0 }}</span>
                  <span>购买方: {{ overviewData.users?.buyer || 0 }}</span>
                </div>
              </div>
            </div>
            
            <div class="stat-item">
              <div class="stat-icon products">
                <el-icon size="24"><Box /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ overviewData.products?.total || 0 }}</h3>
                <p>产品总数</p>
                <div class="stat-details">
                  <span>上架: {{ overviewData.products?.active || 0 }}</span>
                  <span>下架: {{ overviewData.products?.inactive || 0 }}</span>
                </div>
              </div>
            </div>
            
            <div class="stat-item">
              <div class="stat-icon orders">
                <el-icon size="24"><ShoppingCart /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ overviewData.orders?.total || 0 }}</h3>
                <p>订单总数</p>
                <div class="stat-details">
                  <span>待支付: {{ overviewData.orders?.pending || 0 }}</span>
                  <span>已完成: {{ overviewData.orders?.completed || 0 }}</span>
                </div>
              </div>
            </div>
            
            <div class="stat-item">
              <div class="stat-icon plans">
                <el-icon size="24"><Calendar /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ overviewData.breedingPlans?.total || 0 }}</h3>
                <p>养殖计划</p>
                <div class="stat-details">
                  <span>进行中: {{ overviewData.breedingPlans?.inProgress || 0 }}</span>
                  <span>已完成: {{ overviewData.breedingPlans?.completed || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 图表区域 -->
        <el-row :gutter="20" class="charts-row">
          <!-- 销售统计 -->
          <el-col :span="12">
            <el-card class="chart-card" shadow="hover">
              <template #header>
                <span>销售统计</span>
              </template>
              <div class="chart-container" ref="salesChart" v-loading="salesLoading"></div>
            </el-card>
          </el-col>
          
          <!-- 用户分析 -->
          <el-col :span="12">
            <el-card class="chart-card" shadow="hover">
              <template #header>
                <span>用户分析</span>
              </template>
              <div class="chart-container" ref="userChart" v-loading="userLoading"></div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 详细统计表格 -->
        <el-row :gutter="20" class="tables-row">
          <!-- 产品统计 -->
          <el-col :span="12">
            <el-card class="table-card" shadow="hover">
              <template #header>
                <span>产品统计</span>
              </template>
              <el-table :data="productStats" v-loading="productLoading" stripe>
                <el-table-column prop="variety" label="品种" width="120"></el-table-column>
                <el-table-column prop="count" label="数量" width="80"></el-table-column>
                <el-table-column prop="percentage" label="占比" width="100">
                  <template #default="scope">
                    {{ scope.row.percentage }}%
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          
          <!-- 订单统计 -->
          <el-col :span="12">
            <el-card class="table-card" shadow="hover">
              <template #header>
                <span>订单状态分布</span>
              </template>
              <el-table :data="orderStats" v-loading="orderLoading" stripe>
                <el-table-column prop="status" label="状态" width="120"></el-table-column>
                <el-table-column prop="count" label="数量" width="80"></el-table-column>
                <el-table-column prop="percentage" label="占比" width="100">
                  <template #default="scope">
                    {{ scope.row.percentage }}%
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { User, Box, ShoppingCart, Calendar } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../api'

// 响应式数据
const overviewData = ref({})
const productStats = ref([])
const orderStats = ref([])

// 加载状态
const overviewLoading = ref(false)
const salesLoading = ref(false)
const userLoading = ref(false)
const productLoading = ref(false)
const orderLoading = ref(false)

// 图表引用
const salesChart = ref(null)
const userChart = ref(null)

// 图表实例
let salesChartInstance = null
let userChartInstance = null

// 获取概览数据
const getOverviewData = async () => {
  overviewLoading.value = true
  try {
    const response = await statisticsApi.getOverview()
    if (response.data.code === 200) {
      overviewData.value = response.data.data
    }
  } catch (error) {
    console.error('获取概览数据失败:', error)
  } finally {
    overviewLoading.value = false
  }
}

// 获取销售数据
const getSalesData = async () => {
  salesLoading.value = true
  try {
    const response = await statisticsApi.getSales()
    if (response.data.code === 200) {
      const data = response.data.data
      initSalesChart(data)
    }
  } catch (error) {
    console.error('获取销售数据失败:', error)
  } finally {
    salesLoading.value = false
  }
}

// 获取用户数据
const getUserData = async () => {
  userLoading.value = true
  try {
    const response = await statisticsApi.getUsers()
    if (response.data.code === 200) {
      const data = response.data.data
      initUserChart(data)
    }
  } catch (error) {
    console.error('获取用户数据失败:', error)
  } finally {
    userLoading.value = false
  }
}

// 获取产品统计
const getProductStats = async () => {
  productLoading.value = true
  try {
    const response = await statisticsApi.getProducts()
    if (response.data.code === 200) {
      const data = response.data.data
      productStats.value = data.varietyDistribution || []
    }
  } catch (error) {
    console.error('获取产品统计失败:', error)
  } finally {
    productLoading.value = false
  }
}

// 获取订单统计
const getOrderStats = async () => {
  orderLoading.value = true
  try {
    const response = await statisticsApi.getOrders()
    if (response.data.code === 200) {
      const data = response.data.data
      orderStats.value = data.statusDistribution ? 
        Object.entries(data.statusDistribution).map(([status, count]) => ({
          status: getStatusText(status),
          count: count,
          percentage: ((count / data.statusDistribution.total) * 100).toFixed(1)
        })) : []
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  } finally {
    orderLoading.value = false
  }
}

// 初始化销售图表
const initSalesChart = (data) => {
  nextTick(() => {
    if (salesChart.value) {
      salesChartInstance = echarts.init(salesChart.value)
      const option = {
        title: {
          text: '销售趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: [120, 200, 150, 80, 70, 110, 130],
          type: 'line',
          smooth: true,
          areaStyle: {}
        }]
      }
      salesChartInstance.setOption(option)
    }
  })
}

// 初始化用户图表
const initUserChart = (data) => {
  nextTick(() => {
    if (userChart.value) {
      userChartInstance = echarts.init(userChart.value)
      const option = {
        title: {
          text: '用户角色分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        series: [{
          type: 'pie',
          radius: '50%',
          data: [
            { value: data.roleDistribution?.admin || 0, name: '管理员' },
            { value: data.roleDistribution?.seller || 0, name: '销售方' },
            { value: data.roleDistribution?.buyer || 0, name: '购买方' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
      userChartInstance.setOption(option)
    }
  })
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'PENDING_PAYMENT': '待支付',
    'PAID': '已支付',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 刷新概览数据
const refreshOverview = () => {
  getOverviewData()
}

// 页面加载时获取数据
onMounted(() => {
  getOverviewData()
  getSalesData()
  getUserData()
  getProductStats()
  getOrderStats()
})
</script>

<style scoped>
.analytics {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  padding: 0 40px;
}

.header-content h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

.header-content p {
  margin: 8px 0 0 0;
  font-size: 16px;
  opacity: 0.9;
}

.main-content {
  padding: 20px;
}

.overview-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.stat-icon.products {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  color: white;
}

.stat-icon.orders {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  color: white;
}

.stat-icon.plans {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: white;
}

.stat-content h3 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-content p {
  margin: 4px 0 8px 0;
  color: #666;
  font-size: 14px;
}

.stat-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-details span {
  font-size: 12px;
  color: #999;
}

.charts-row, .tables-row {
  margin-bottom: 20px;
}

.chart-card, .table-card {
  height: 400px;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.table-card {
  height: auto;
}

.el-table {
  margin-top: 10px;
}
</style>
