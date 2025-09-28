<template>
  <div class="import-test">
    <h2>导入测试页面</h2>
    <p>测试API导入是否正常工作</p>
    
    <el-button @click="testImport" type="primary">测试API导入</el-button>
    
    <div v-if="testResult" class="test-result">
      <el-alert :title="testResult" :type="testResult.includes('成功') ? 'success' : 'error'" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { productApi, orderApi } from '../api'

const testResult = ref('')

const testImport = () => {
  try {
    // 测试API对象是否存在
    if (productApi && orderApi) {
      testResult.value = 'API导入成功！productApi和orderApi都可以正常访问'
      ElMessage.success('API导入测试成功')
    } else {
      testResult.value = 'API导入失败：API对象未定义'
      ElMessage.error('API导入测试失败')
    }
  } catch (error) {
    testResult.value = `API导入失败：${error.message}`
    ElMessage.error('API导入测试失败')
  }
}
</script>

<style scoped>
.import-test {
  padding: 20px;
  text-align: center;
}

.test-result {
  margin-top: 20px;
}
</style>
