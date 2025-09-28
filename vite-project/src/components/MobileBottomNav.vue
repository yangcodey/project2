<template>
  <div class="mobile-bottom-nav" v-if="isMobile">
    <div class="nav-item" @click="goToHome">
      <el-icon><House /></el-icon>
      <span>首页</span>
    </div>
    
    <div class="nav-item" @click="goToProducts">
      <el-icon><ShoppingCart /></el-icon>
      <span>商品</span>
    </div>
    
    <div class="nav-item" @click="goToFavorites">
      <el-icon><Star /></el-icon>
      <span>收藏</span>
    </div>
    
    <div class="nav-item" @click="goToOrders">
      <el-icon><List /></el-icon>
      <span>订单</span>
    </div>
    
    <div class="nav-item" @click="goToProfile">
      <el-icon><User /></el-icon>
      <span>我的</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { House, ShoppingCart, Star, List, User } from '@element-plus/icons-vue'

const router = useRouter()
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

const goToHome = () => {
  router.push('/')
}

const goToProducts = () => {
  router.push('/products')
}

const goToFavorites = () => {
  router.push('/favorites')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToProfile = () => {
  router.push('/profile')
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.mobile-bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 8px 0;
  z-index: 1000;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  transition: color 0.3s;
  min-width: 60px;
}

.nav-item:hover {
  color: #409EFF;
}

.nav-item .el-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

.nav-item span {
  font-size: 12px;
  color: #666;
}

.nav-item:hover span {
  color: #409EFF;
}

/* 桌面端隐藏 */
@media (min-width: 769px) {
  .mobile-bottom-nav {
    display: none;
  }
}

/* 为移动端底部导航预留空间 */
@media (max-width: 768px) {
  body {
    padding-bottom: 60px;
  }
}
</style>
