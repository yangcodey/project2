<template>
  <div class="icon-test">
    <h1>图标测试</h1>
    <div class="icon-grid">
      <div class="icon-item">
        <el-icon size="32"><ShoppingCart /></el-icon>
        <span>ShoppingCart</span>
      </div>
      <div class="icon-item">
        <el-icon size="32"><Position /></el-icon>
        <span>Position</span>
      </div>
      <div class="icon-item">
        <el-icon size="32"><Menu /></el-icon>
        <span>Menu</span>
      </div>
      <div class="icon-item">
        <el-icon size="32"><User /></el-icon>
        <span>User</span>
      </div>
      <div class="icon-item">
        <el-icon size="32"><Box /></el-icon>
        <span>Box</span>
      </div>
      <div class="icon-item">
        <el-icon size="32"><Money /></el-icon>
        <span>Money</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ShoppingCart, Position, Menu, User, Box, Money } from '@element-plus/icons-vue'
</script>

<style scoped>
.icon-test {
  padding: 20px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.icon-item span {
  font-size: 14px;
  color: #666;
}
</style>
