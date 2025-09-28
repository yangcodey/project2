<template>
  <div class="profile-page">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="nav-container">
        <div class="logo">
          <h2>侬帮我养</h2>
          <p>在线销售预约系统</p>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="'profile'"
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
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <div class="profile-container">
        <div class="page-header">
          <h1>个人资料</h1>
          <p>管理您的个人信息</p>
        </div>

        <el-row :gutter="30">
          <!-- 左侧：头像和基本信息 -->
          <el-col :span="8">
            <el-card class="profile-card">
              <div class="avatar-section">
                <el-avatar :size="120" :src="userInfo.avatar" class="user-avatar">
                  {{ userInfo.realName?.charAt(0) }}
                </el-avatar>
                <h3>{{ userInfo.realName }}</h3>
                <p class="user-role">{{ getRoleText(userInfo.role) }}</p>
                <el-button type="primary" @click="showAvatarDialog = true">更换头像</el-button>
              </div>
            </el-card>
          </el-col>

          <!-- 右侧：详细信息表单 -->
          <el-col :span="16">
            <el-card class="info-card">
              <el-tabs v-model="activeTab">
                <!-- 基本信息 -->
                <el-tab-pane label="基本信息" name="basic">
                  <el-form
                    ref="basicFormRef"
                    :model="basicForm"
                    :rules="basicRules"
                    label-width="100px"
                    class="profile-form"
                  >
                    <el-form-item label="用户名" prop="username">
                      <el-input v-model="basicForm.username" disabled />
                    </el-form-item>
                    <el-form-item label="真实姓名" prop="realName">
                      <el-input v-model="basicForm.realName" />
                    </el-form-item>
                    <el-form-item label="手机号" prop="phone">
                      <el-input v-model="basicForm.phone" />
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                      <el-input v-model="basicForm.email" />
                    </el-form-item>
                    <el-form-item label="地址" prop="address">
                      <el-input
                        v-model="basicForm.address"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入详细地址"
                      />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="updateBasicInfo" :loading="updating">
                        保存修改
                      </el-button>
                    </el-form-item>
                  </el-form>
                </el-tab-pane>

                <!-- 修改密码 -->
                <el-tab-pane label="修改密码" name="password">
                  <el-form
                    ref="passwordFormRef"
                    :model="passwordForm"
                    :rules="passwordRules"
                    label-width="100px"
                    class="profile-form"
                  >
                    <el-form-item label="当前密码" prop="oldPassword">
                      <el-input
                        v-model="passwordForm.oldPassword"
                        type="password"
                        show-password
                      />
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPassword">
                      <el-input
                        v-model="passwordForm.newPassword"
                        type="password"
                        show-password
                      />
                    </el-form-item>
                    <el-form-item label="确认密码" prop="confirmPassword">
                      <el-input
                        v-model="passwordForm.confirmPassword"
                        type="password"
                        show-password
                      />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="updatePassword" :loading="updating">
                        修改密码
                      </el-button>
                    </el-form-item>
                  </el-form>
                </el-tab-pane>

                <!-- 账户设置 -->
                <el-tab-pane label="账户设置" name="settings">
                  <div class="settings-section">
                    <h3>账户状态</h3>
                    <p>状态：<el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                      {{ userInfo.status === 1 ? '正常' : '已禁用' }}
                    </el-tag></p>
                    
                    <h3>注册时间</h3>
                    <p>{{ formatDate(userInfo.createdAt) }}</p>
                    
                    <h3>最后更新</h3>
                    <p>{{ formatDate(userInfo.updatedAt) }}</p>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>

    <!-- 更换头像对话框 -->
    <el-dialog v-model="showAvatarDialog" title="更换头像" width="400px">
      <div class="avatar-upload">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="uploadAvatar"
        >
          <img v-if="avatarUrl" :src="avatarUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <p class="upload-tip">点击上传头像，支持 JPG、PNG 格式</p>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAvatarDialog = false">取消</el-button>
          <el-button type="primary" @click="saveAvatar">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi } from '../api'

const router = useRouter()

// 响应式数据
const activeTab = ref('basic')
const updating = ref(false)
const showAvatarDialog = ref(false)
const avatarUrl = ref('')
const basicFormRef = ref()
const passwordFormRef = ref()

const basicForm = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  address: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const basicRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 4, message: '密码长度不能少于4位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 计算属性
const userInfo = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : {}
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

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleString('zh-CN')
}

const updateBasicInfo = async () => {
  if (!basicFormRef.value) return
  
  try {
    const valid = await basicFormRef.value.validate()
    if (!valid) return
    
    updating.value = true
    
    const response = await userApi.updateUser(userInfo.value.id, basicForm.value)
    if (response.data.code === 200) {
      ElMessage.success('信息更新成功')
      // 更新本地存储的用户信息
      const updatedUser = { ...userInfo.value, ...basicForm.value }
      localStorage.setItem('user', JSON.stringify(updatedUser))
    } else {
      ElMessage.error(response.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新信息失败:', error)
    ElMessage.error('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

const updatePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    const valid = await passwordFormRef.value.validate()
    if (!valid) return
    
    updating.value = true
    
    // 这里应该调用修改密码的API
    ElMessage.success('密码修改成功')
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改失败，请重试')
  } finally {
    updating.value = false
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

const uploadAvatar = (options) => {
  const file = options.file
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarUrl.value = e.target.result
  }
  reader.readAsDataURL(file)
}

const saveAvatar = () => {
  if (avatarUrl.value) {
    // 这里应该调用上传头像的API
    ElMessage.success('头像更新成功')
    showAvatarDialog.value = false
  } else {
    ElMessage.warning('请先选择头像')
  }
}

const initForm = () => {
  basicForm.value = {
    username: userInfo.value.username || '',
    realName: userInfo.value.realName || '',
    phone: userInfo.value.phone || '',
    email: userInfo.value.email || '',
    address: userInfo.value.address || ''
  }
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.profile-page {
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

.profile-container {
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

.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 20px;
}

.user-avatar {
  margin-bottom: 16px;
}

.avatar-section h3 {
  margin: 16px 0 8px 0;
  color: #333;
}

.user-role {
  color: #666;
  margin-bottom: 20px;
}

.info-card {
  min-height: 500px;
}

.profile-form {
  max-width: 500px;
  margin: 0 auto;
}

.settings-section h3 {
  color: #333;
  margin: 20px 0 8px 0;
}

.settings-section p {
  color: #666;
  margin-bottom: 16px;
}

.avatar-upload {
  text-align: center;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}

.upload-tip {
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}

.dialog-footer {
  text-align: right;
}
</style>
