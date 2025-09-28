<template>
  <div class="notification-center">
    <!-- 通知铃铛 -->
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
      <el-button @click="showNotifications = true" :icon="Bell" circle />
    </el-badge>

    <!-- 通知抽屉 -->
    <el-drawer
      v-model="showNotifications"
      title="消息通知"
      direction="rtl"
      size="400px"
      @close="handleDrawerClose"
    >
      <!-- 通知头部 -->
      <div class="notification-header">
        <div class="header-actions">
          <el-button size="small" @click="markAllAsRead" :disabled="unreadCount === 0">
            <el-icon><Check /></el-icon>
            全部已读
          </el-button>
          <el-button size="small" @click="clearAllNotifications" type="danger">
            <el-icon><Delete /></el-icon>
            清空
          </el-button>
        </div>
        
        <!-- 通知类型筛选 -->
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="订单" name="ORDER" />
          <el-tab-pane label="系统" name="SYSTEM" />
          <el-tab-pane label="库存" name="STOCK" />
          <el-tab-pane label="促销" name="PROMOTION" />
        </el-tabs>
      </div>

      <!-- 通知列表 -->
      <div class="notification-list">
        <div v-if="notifications.length === 0" class="empty-notifications">
          <el-empty description="暂无通知" />
        </div>
        
        <div v-else>
          <div
            v-for="notification in notifications"
            :key="notification.id"
            class="notification-item"
            :class="{ 'unread': notification.status === 'UNREAD' }"
            @click="handleNotificationClick(notification)"
          >
            <div class="notification-icon">
              <el-icon :color="getTypeColor(notification.type)">
                <component :is="getTypeIcon(notification.type)" />
              </el-icon>
            </div>
            
            <div class="notification-content">
              <div class="notification-header">
                <h4 class="notification-title">{{ notification.title }}</h4>
                <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
              </div>
              
              <p class="notification-text">{{ notification.content }}</p>
              
              <div class="notification-actions" v-if="notification.actionUrl">
                <el-button size="small" type="primary" @click.stop="handleAction(notification)">
                  {{ notification.actionText || '查看详情' }}
                </el-button>
              </div>
            </div>
            
            <div class="notification-actions-right">
              <el-button
                v-if="notification.status === 'UNREAD'"
                size="small"
                @click.stop="markAsRead(notification.id)"
                :icon="Check"
              >
                已读
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click.stop="deleteNotification(notification.id)"
                :icon="Delete"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalCount > pageSize" class="notification-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalCount"
          layout="prev, pager, next"
          small
          @current-change="handlePageChange"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Check, Delete, ShoppingCart, Setting, Warning, Present } from '@element-plus/icons-vue'
import { notificationApi } from '../api'

const router = useRouter()

// 响应式数据
const showNotifications = ref(false)
const notifications = ref([])
const unreadCount = ref(0)
const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const loading = ref(false)

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
})

// 方法
const loadNotifications = async () => {
  if (!userInfo.value.id) return
  
  try {
    loading.value = true
    const response = await notificationApi.getUserNotifications({
      userId: userInfo.value.id,
      status: activeTab.value === 'all' ? null : activeTab.value,
      page: currentPage.value,
      size: pageSize.value
    })
    
    if (response.data.code === 200) {
      notifications.value = response.data.data
      totalCount.value = response.data.total || 0
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  if (!userInfo.value.id) return
  
  try {
    const response = await notificationApi.getUnreadCount(userInfo.value.id)
    if (response.data.code === 200) {
      unreadCount.value = response.data.data
    }
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  loadNotifications()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadNotifications()
}

const handleDrawerClose = () => {
  showNotifications.value = false
}

const handleNotificationClick = (notification) => {
  if (notification.status === 'UNREAD') {
    markAsRead(notification.id)
  }
}

const handleAction = (notification) => {
  if (notification.actionUrl) {
    router.push(notification.actionUrl)
    showNotifications.value = false
  }
}

const markAsRead = async (notificationId) => {
  try {
    const response = await notificationApi.markAsRead(notificationId)
    if (response.data.code === 200) {
      // 更新本地状态
      const notification = notifications.value.find(n => n.id === notificationId)
      if (notification) {
        notification.status = 'READ'
        notification.readAt = new Date().toISOString()
      }
      unreadCount.value = Math.max(0, unreadCount.value - 1)
      ElMessage.success('标记已读成功')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

const markAllAsRead = async () => {
  try {
    const response = await notificationApi.markAllAsRead(userInfo.value.id)
    if (response.data.code === 200) {
      // 更新本地状态
      notifications.value.forEach(notification => {
        if (notification.status === 'UNREAD') {
          notification.status = 'READ'
          notification.readAt = new Date().toISOString()
        }
      })
      unreadCount.value = 0
      ElMessage.success('全部标记已读成功')
    }
  } catch (error) {
    console.error('全部标记已读失败:', error)
    ElMessage.error('全部标记已读失败')
  }
}

const deleteNotification = async (notificationId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await notificationApi.deleteNotification(notificationId)
    if (response.data.code === 200) {
      // 更新本地状态
      const index = notifications.value.findIndex(n => n.id === notificationId)
      if (index > -1) {
        notifications.value.splice(index, 1)
        if (notifications.value[index]?.status === 'UNREAD') {
          unreadCount.value = Math.max(0, unreadCount.value - 1)
        }
      }
      ElMessage.success('删除成功')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除通知失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const clearAllNotifications = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有通知吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用清空所有通知的API
    notifications.value = []
    unreadCount.value = 0
    ElMessage.success('清空成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空通知失败:', error)
      ElMessage.error('清空失败')
    }
  }
}

const getTypeIcon = (type) => {
  const iconMap = {
    'ORDER': ShoppingCart,
    'SYSTEM': Setting,
    'STOCK': Warning,
    'PROMOTION': Present
  }
  return iconMap[type] || Setting
}

const getTypeColor = (type) => {
  const colorMap = {
    'ORDER': '#409EFF',
    'SYSTEM': '#909399',
    'STOCK': '#F56C6C',
    'PROMOTION': '#E6A23C'
  }
  return colorMap[type] || '#909399'
}

const formatTime = (time) => {
  const now = new Date()
  const notificationTime = new Date(time)
  const diff = now - notificationTime
  
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
    return notificationTime.toLocaleDateString('zh-CN')
  }
}

// 监听抽屉打开
watch(showNotifications, (newVal) => {
  if (newVal) {
    loadNotifications()
  }
})

onMounted(() => {
  loadUnreadCount()
  // 定期刷新未读数量
  setInterval(loadUnreadCount, 30000) // 30秒刷新一次
})
</script>

<style scoped>
.notification-center {
  position: relative;
}

.notification-badge {
  margin-right: 10px;
}

.notification-header {
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.notification-list {
  max-height: 500px;
  overflow-y: auto;
}

.empty-notifications {
  text-align: center;
  padding: 40px 0;
}

.notification-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f5f5f5;
}

.notification-item.unread {
  background-color: #f0f9ff;
  border-left: 3px solid #409EFF;
}

.notification-icon {
  margin-right: 12px;
  margin-top: 2px;
}

.notification-content {
  flex: 1;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.notification-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.notification-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.notification-text {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #666;
  line-height: 1.4;
}

.notification-actions {
  margin-top: 8px;
}

.notification-actions-right {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-left: 10px;
}

.notification-actions-right .el-button {
  font-size: 12px;
  padding: 4px 8px;
}

.notification-pagination {
  margin-top: 20px;
  text-align: center;
}

/* 移动端响应式 */
@media (max-width: 768px) {
  .notification-center {
    margin-right: 5px;
  }
  
  .notification-badge .el-button {
    width: 40px;
    height: 40px;
  }
  
  .notification-header {
    margin-bottom: 15px;
  }
  
  .header-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .notification-item {
    padding: 12px;
  }
  
  .notification-content {
    margin-right: 8px;
  }
  
  .notification-title {
    font-size: 14px;
  }
  
  .notification-text {
    font-size: 12px;
  }
  
  .notification-actions-right {
    flex-direction: column;
    gap: 4px;
    margin-left: 8px;
  }
  
  .notification-actions-right .el-button {
    font-size: 11px;
    padding: 4px 6px;
  }
}

@media (max-width: 480px) {
  .notification-center {
    margin-right: 0;
  }
  
  .notification-badge .el-button {
    width: 36px;
    height: 36px;
  }
  
  .notification-item {
    padding: 10px;
  }
  
  .notification-icon {
    margin-right: 8px;
  }
  
  .notification-content {
    margin-right: 6px;
  }
  
  .notification-title {
    font-size: 13px;
  }
  
  .notification-text {
    font-size: 11px;
  }
  
  .notification-actions-right {
    margin-left: 6px;
  }
  
  .notification-actions-right .el-button {
    font-size: 10px;
    padding: 3px 5px;
  }
}
</style>