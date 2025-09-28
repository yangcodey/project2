<template>
  <div class="product-reviews">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>产品评价</h2>
      <p>查看和管理产品评价</p>
    </div>

    <!-- 评价统计 -->
    <div class="review-stats">
      <el-card>
        <div class="stats-content">
          <div class="rating-overview">
            <div class="rating-score">
              <span class="score">{{ averageRating }}</span>
              <div class="stars">
                <el-rate
                  v-model="averageRating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </div>
              <p class="rating-text">基于 {{ totalReviews }} 条评价</p>
            </div>
          </div>
          
          <div class="rating-breakdown">
            <div v-for="i in 5" :key="i" class="rating-bar">
              <span class="star-label">{{ 6-i }}星</span>
              <el-progress
                :percentage="getRatingPercentage(6-i)"
                :color="getRatingColor(6-i)"
                :show-text="false"
                class="progress-bar"
              />
              <span class="count">{{ getRatingCount(6-i) }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 评价筛选 -->
    <div class="review-filters">
      <el-card>
        <div class="filter-content">
          <el-select v-model="selectedRating" placeholder="选择评分" @change="handleFilterChange">
            <el-option label="全部评分" value="" />
            <el-option label="5星" value="5" />
            <el-option label="4星" value="4" />
            <el-option label="3星" value="3" />
            <el-option label="2星" value="2" />
            <el-option label="1星" value="1" />
          </el-select>
          
          <el-select v-model="sortBy" placeholder="排序方式" @change="handleFilterChange">
            <el-option label="最新评价" value="newest" />
            <el-option label="最旧评价" value="oldest" />
            <el-option label="评分最高" value="highest" />
            <el-option label="评分最低" value="lowest" />
          </el-select>
          
          <el-button @click="showReviewForm = true" type="primary" :icon="Edit">
            写评价
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 评价列表 -->
    <div class="review-list">
      <el-card v-for="review in filteredReviews" :key="review.id" class="review-item">
        <div class="review-header">
          <div class="user-info">
            <el-avatar :size="40" :src="review.userAvatar">
              {{ review.userName?.charAt(0) }}
            </el-avatar>
            <div class="user-details">
              <h4>{{ review.userName }}</h4>
              <p class="review-time">{{ formatTime(review.createdAt) }}</p>
            </div>
          </div>
          <div class="rating">
            <el-rate
              v-model="review.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
          </div>
        </div>
        
        <div class="review-content">
          <p class="review-text">{{ review.content }}</p>
          
          <!-- 评价图片 -->
          <div v-if="review.images && review.images.length > 0" class="review-images">
            <el-image
              v-for="(image, index) in review.images"
              :key="index"
              :src="image"
              :preview-src-list="review.images"
              :initial-index="index"
              class="review-image"
              fit="cover"
            />
          </div>
        </div>
        
        <div class="review-actions">
          <el-button size="small" :icon="Thumb" @click="likeReview(review.id)">
            有用 ({{ review.likeCount || 0 }})
          </el-button>
          <el-button size="small" :icon="ChatDotRound" @click="replyReview(review.id)">
            回复
          </el-button>
          <el-button 
            v-if="canDeleteReview(review)" 
            size="small" 
            type="danger" 
            :icon="Delete"
            @click="deleteReview(review.id)"
          >
            删除
          </el-button>
        </div>
      </el-card>
      
      <!-- 空状态 -->
      <el-empty v-if="filteredReviews.length === 0" description="暂无评价" />
    </div>

    <!-- 写评价对话框 -->
    <el-dialog
      v-model="showReviewForm"
      title="写评价"
      width="600px"
      @close="resetReviewForm"
    >
      <el-form :model="reviewForm" :rules="reviewRules" ref="reviewFormRef" label-width="80px">
        <el-form-item label="评分" prop="rating">
          <el-rate
            v-model="reviewForm.rating"
            show-text
            :texts="['极差', '失望', '一般', '满意', '惊喜']"
            text-color="#ff9900"
          />
        </el-form-item>
        
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您的使用体验..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="上传图片">
          <el-upload
            v-model:file-list="reviewForm.images"
            action="/api/upload"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            :limit="6"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showReviewForm = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submitting">
          提交评价
        </el-button>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" title="图片预览">
      <img :src="previewImage" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Thumb, ChatDotRound, Delete, Plus } from '@element-plus/icons-vue'
import { reviewApi } from '../api'

const route = useRoute()

// 响应式数据
const reviews = ref([])
const selectedRating = ref('')
const sortBy = ref('newest')
const showReviewForm = ref(false)
const submitting = ref(false)
const previewVisible = ref(false)
const previewImage = ref('')

// 评价表单
const reviewForm = ref({
  productId: null,
  rating: 5,
  content: '',
  images: []
})

const reviewFormRef = ref()

const reviewRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字符', trigger: 'blur' }
  ]
}

// 计算属性
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const total = reviews.value.reduce((sum, review) => sum + review.rating, 0)
  return (total / reviews.value.length).toFixed(1)
})

const totalReviews = computed(() => reviews.value.length)

const filteredReviews = computed(() => {
  let filtered = reviews.value

  // 按评分筛选
  if (selectedRating.value) {
    filtered = filtered.filter(review => review.rating === parseInt(selectedRating.value))
  }

  // 排序
  switch (sortBy.value) {
    case 'newest':
      filtered = filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      break
    case 'oldest':
      filtered = filtered.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
      break
    case 'highest':
      filtered = filtered.sort((a, b) => b.rating - a.rating)
      break
    case 'lowest':
      filtered = filtered.sort((a, b) => a.rating - b.rating)
      break
  }

  return filtered
})

// 方法
const loadReviews = async (productId) => {
  try {
    const response = await reviewApi.getProductReviews(productId)
    if (response.data.code === 200) {
      reviews.value = response.data.data
    }
  } catch (error) {
    console.error('加载评价失败:', error)
    ElMessage.error('加载评价失败')
  }
}

const getRatingPercentage = (rating) => {
  const count = getRatingCount(rating)
  return totalReviews.value > 0 ? (count / totalReviews.value) * 100 : 0
}

const getRatingCount = (rating) => {
  return reviews.value.filter(review => review.rating === rating).length
}

const getRatingColor = (rating) => {
  const colors = ['#f56c6c', '#e6a23c', '#e6a23c', '#67c23a', '#67c23a']
  return colors[rating - 1]
}

const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN')
}

const handleFilterChange = () => {
  // 筛选逻辑在computed中处理
}

const likeReview = async (reviewId) => {
  try {
    const response = await reviewApi.likeReview(reviewId)
    if (response.data.code === 200) {
      ElMessage.success('点赞成功')
      // 更新本地数据
      const review = reviews.value.find(r => r.id === reviewId)
      if (review) {
        review.likeCount = (review.likeCount || 0) + 1
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

const replyReview = (reviewId) => {
  // 实现回复功能
  ElMessage.info('回复功能开发中...')
}

const canDeleteReview = (review) => {
  // 检查是否可以删除评价（管理员或评价作者）
  const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
  return currentUser.role === 'ADMIN' || currentUser.id === review.userId
}

const deleteReview = async (reviewId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await reviewApi.deleteReview(reviewId)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      reviews.value = reviews.value.filter(r => r.id !== reviewId)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评价失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const resetReviewForm = () => {
  reviewForm.value = {
    productId: null,
    rating: 5,
    content: '',
    images: []
  }
  if (reviewFormRef.value) {
    reviewFormRef.value.resetFields()
  }
}

const submitReview = async () => {
  if (!reviewFormRef.value) return
  
  try {
    await reviewFormRef.value.validate()
    
    submitting.value = true
    const response = await reviewApi.createReview(reviewForm.value)
    
    if (response.data.code === 200) {
      ElMessage.success('评价提交成功')
      showReviewForm.value = false
      resetReviewForm()
      loadReviews(reviewForm.value.productId)
    } else {
      ElMessage.error(response.data.message || '评价提交失败')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error('评价提交失败')
  } finally {
    submitting.value = false
  }
}

const handlePictureCardPreview = (file) => {
  previewImage.value = file.url
  previewVisible.value = true
}

const handleRemove = (file, fileList) => {
  reviewForm.value.images = fileList
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 暴露方法给父组件
defineExpose({
  loadReviews
})

onMounted(() => {
  // 从路由参数获取产品ID
  const route = useRoute()
  if (route.params.productId) {
    reviewForm.value.productId = route.params.productId
    loadReviews(route.params.productId)
  }
})
</script>

<style scoped>
.product-reviews {
  max-width: 1200px;
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

.review-stats {
  margin-bottom: 20px;
}

.stats-content {
  display: flex;
  gap: 40px;
  align-items: center;
}

.rating-overview {
  text-align: center;
}

.rating-score .score {
  font-size: 48px;
  font-weight: bold;
  color: #ff9900;
  display: block;
}

.rating-text {
  color: #666;
  margin-top: 10px;
}

.rating-breakdown {
  flex: 1;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.star-label {
  width: 40px;
  font-size: 14px;
  color: #666;
}

.progress-bar {
  flex: 1;
}

.count {
  width: 30px;
  text-align: right;
  font-size: 14px;
  color: #666;
}

.review-filters {
  margin-bottom: 20px;
}

.filter-content {
  display: flex;
  gap: 20px;
  align-items: center;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  margin-bottom: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.review-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.review-content {
  margin-bottom: 15px;
}

.review-text {
  line-height: 1.6;
  color: #333;
  margin-bottom: 15px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
}

.review-actions {
  display: flex;
  gap: 10px;
}

.review-actions .el-button {
  font-size: 12px;
}
</style>
