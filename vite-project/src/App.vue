<template>
  <div id="app">
    <router-view />
    <MobileBottomNav />
  </div>
</template>

<script setup>
import { onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import MobileBottomNav from './components/MobileBottomNav.vue'

const router = useRouter()
const route = useRoute()

// 检查用户权限
const checkUserPermission = (to) => {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  
  if (!token || !user) {
    // 用户未登录
    if (to.meta.requiresAuth) {
      ElMessage.warning('请先登录')
      return false
    }
    return true
  }
  
  const userInfo = JSON.parse(user)
  
  // 检查角色权限
  if (to.meta.requiresAdmin && userInfo.role !== 'ADMIN') {
    ElMessage.error('需要管理员权限')
    return false
  }
  
  if (to.meta.requiresSeller && userInfo.role !== 'SELLER') {
    ElMessage.error('需要销售方权限')
    return false
  }
  
  if (to.meta.requiresBuyer && userInfo.role !== 'BUYER') {
    ElMessage.error('需要购买方权限')
    return false
  }
  
  return true
}

// 路由守卫
watch(route, (to, from) => {
  if (!checkUserPermission(to)) {
    // 根据用户角色重定向到合适的页面
    const user = localStorage.getItem('user')
    if (user) {
      const userInfo = JSON.parse(user)
      switch (userInfo.role) {
        case 'ADMIN':
          router.push('/admin')
          break
        case 'SELLER':
          router.push('/seller-dashboard')
          break
        case 'BUYER':
          router.push('/buyer-dashboard')
          break
        default:
          router.push('/home')
      }
    } else {
      router.push('/home')
    }
  }
}, { immediate: true })

onMounted(() => {
  // 检查用户是否已登录
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  
  if (token && user) {
    // 用户已登录，根据角色重定向到合适的仪表盘
    const userInfo = JSON.parse(user)
    const currentPath = router.currentRoute.value.path
    
    // 如果用户访问根路径，根据角色重定向
    if (currentPath === '/' || currentPath === '/home') {
      switch (userInfo.role) {
        case 'ADMIN':
          router.push('/admin')
          break
        case 'SELLER':
          router.push('/seller-dashboard')
          break
        case 'BUYER':
          router.push('/buyer-dashboard')
          break
        default:
          // 保持当前页面
          break
      }
    }
  } else {
    // 用户未登录，重定向到首页
    if (router.currentRoute.value.path !== '/home' && 
        router.currentRoute.value.path !== '/login' && 
        router.currentRoute.value.path !== '/register' &&
        router.currentRoute.value.path !== '/products' &&
        router.currentRoute.value.path !== '/feature-test') {
      router.push('/home')
    }
  }
})
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: #f5f5f5;
}
</style>
