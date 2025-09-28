<template>
  <div class="address-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>收货地址管理</h2>
      <p>管理您的收货地址信息</p>
    </div>

    <!-- 添加地址按钮 -->
    <div class="add-address-section">
      <el-button type="primary" @click="showAddDialog = true" :icon="Plus">
        添加新地址
      </el-button>
    </div>

    <!-- 地址列表 -->
    <div class="address-list">
      <el-card v-for="address in addresses" :key="address.id" class="address-card">
        <div class="address-content">
          <div class="address-info">
            <div class="address-header">
              <h4>{{ address.contactName }}</h4>
              <span class="phone">{{ address.phone }}</span>
              <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
            </div>
            <p class="address-detail">{{ address.fullAddress }}</p>
            <p class="address-note" v-if="address.notes">{{ address.notes }}</p>
          </div>
          
          <div class="address-actions">
            <el-button size="small" @click="editAddress(address)" :icon="Edit">
              编辑
            </el-button>
            <el-button 
              v-if="!address.isDefault" 
              size="small" 
              @click="setDefaultAddress(address.id)"
              :icon="Star"
            >
              设为默认
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteAddress(address.id)"
              :icon="Delete"
            >
              删除
            </el-button>
          </div>
        </div>
      </el-card>
      
      <!-- 空状态 -->
      <el-empty v-if="addresses.length === 0" description="暂无收货地址" />
    </div>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingAddress ? '编辑地址' : '添加地址'"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="80px">
        <el-form-item label="收货人" prop="contactName">
          <el-input v-model="addressForm.contactName" placeholder="请输入收货人姓名" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            placeholder="请选择省市区"
            @change="handleRegionChange"
          />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址（街道、门牌号等）"
          />
        </el-form-item>
        
        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="addressForm.notes"
            placeholder="选填，如：公司地址、家等"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress" :loading="saving">
          {{ editingAddress ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Star, Delete } from '@element-plus/icons-vue'
import { addressApi } from '../api'

// 响应式数据
const addresses = ref([])
const showAddDialog = ref(false)
const editingAddress = ref(null)
const saving = ref(false)

// 地址表单
const addressForm = ref({
  contactName: '',
  phone: '',
  region: [],
  detailAddress: '',
  notes: '',
  isDefault: false
})

const addressFormRef = ref()

// 表单验证规则
const addressRules = {
  contactName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 地区选项（简化版）
const regionOptions = ref([
  {
    value: 'beijing',
    label: '北京市',
    children: [
      { value: 'beijing-dongcheng', label: '东城区' },
      { value: 'beijing-xicheng', label: '西城区' },
      { value: 'beijing-chaoyang', label: '朝阳区' },
      { value: 'beijing-haidian', label: '海淀区' }
    ]
  },
  {
    value: 'shanghai',
    label: '上海市',
    children: [
      { value: 'shanghai-huangpu', label: '黄浦区' },
      { value: 'shanghai-xuhui', label: '徐汇区' },
      { value: 'shanghai-changning', label: '长宁区' },
      { value: 'shanghai-jingan', label: '静安区' }
    ]
  },
  {
    value: 'guangdong',
    label: '广东省',
    children: [
      { value: 'guangzhou', label: '广州市' },
      { value: 'shenzhen', label: '深圳市' },
      { value: 'zhuhai', label: '珠海市' },
      { value: 'foshan', label: '佛山市' }
    ]
  },
  {
    value: 'shandong',
    label: '山东省',
    children: [
      { value: 'jinan', label: '济南市' },
      { value: 'qingdao', label: '青岛市' },
      { value: 'yantai', label: '烟台市' },
      { value: 'weifang', label: '潍坊市' }
    ]
  }
])

// 方法
const loadAddresses = async () => {
  try {
    const response = await addressApi.getAddresses()
    if (response.data.code === 200) {
      addresses.value = response.data.data
    }
  } catch (error) {
    console.error('加载地址失败:', error)
    ElMessage.error('加载地址失败')
  }
}

const editAddress = (address) => {
  editingAddress.value = address
  addressForm.value = {
    contactName: address.contactName,
    phone: address.phone,
    region: address.region || [],
    detailAddress: address.detailAddress,
    notes: address.notes || '',
    isDefault: address.isDefault
  }
  showAddDialog.value = true
}

const setDefaultAddress = async (addressId) => {
  try {
    const response = await addressApi.setDefaultAddress(addressId)
    if (response.data.code === 200) {
      ElMessage.success('设置默认地址成功')
      loadAddresses()
    }
  } catch (error) {
    console.error('设置默认地址失败:', error)
    ElMessage.error('设置默认地址失败')
  }
}

const deleteAddress = async (addressId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await addressApi.deleteAddress(addressId)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadAddresses()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleRegionChange = (value) => {
  // 处理地区选择变化
  console.log('选择的地区:', value)
}

const saveAddress = async () => {
  if (!addressFormRef.value) return
  
  try {
    await addressFormRef.value.validate()
    
    saving.value = true
    
    const addressData = {
      ...addressForm.value,
      fullAddress: getFullAddress(),
      region: addressForm.value.region.join(' ')
    }
    
    let response
    if (editingAddress.value) {
      response = await addressApi.updateAddress(editingAddress.value.id, addressData)
    } else {
      response = await addressApi.createAddress(addressData)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(editingAddress.value ? '地址更新成功' : '地址添加成功')
      showAddDialog.value = false
      resetForm()
      loadAddresses()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('保存地址失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const getFullAddress = () => {
  const region = addressForm.value.region.join('')
  return region + addressForm.value.detailAddress
}

const resetForm = () => {
  addressForm.value = {
    contactName: '',
    phone: '',
    region: [],
    detailAddress: '',
    notes: '',
    isDefault: false
  }
  editingAddress.value = null
  if (addressFormRef.value) {
    addressFormRef.value.resetFields()
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.address-management {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.add-address-section {
  margin-bottom: 20px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-card {
  margin-bottom: 0;
}

.address-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.address-header h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.phone {
  color: #666;
  font-size: 14px;
}

.address-detail {
  color: #333;
  margin: 8px 0;
  line-height: 1.5;
}

.address-note {
  color: #999;
  font-size: 12px;
  margin: 4px 0 0 0;
}

.address-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.address-actions .el-button {
  font-size: 12px;
}
</style>
