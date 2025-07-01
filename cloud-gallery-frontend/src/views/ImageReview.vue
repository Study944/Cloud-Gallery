<template>
  <div class="admin-page">
    <header class="admin-header">
      <h1>图片审核</h1>
      <button class="back-btn" @click="goBack">返回首页</button>
    </header>
    
    <div class="review-filters">
      <select v-model="status" @change="fetchImages">
        <option value="pending">待审核</option>
        <option value="approved">已通过</option>
        <option value="rejected">已拒绝</option>
        <option value="all">全部</option>
      </select>
      <div class="search-box">
        <input v-model="search" placeholder="搜索图片名称或ID..." @keyup.enter="fetchImages">
        <button @click="fetchImages">搜索</button>
      </div>
    </div>
    
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
      <span>加载中...</span>
    </div>
    
    <div v-else-if="images.length === 0" class="no-results">
      <p>没有找到图片</p>
    </div>
    
    <div class="image-grid" v-else>
      <div class="image-card" v-for="image in images" :key="image.id">
        <img :src="image.url" :alt="image.name" class="review-image">
        <div class="image-info">
          <h3>{{ image.name }}</h3>
          <p>上传者: {{ image.uploaderName }}</p>
          <p>上传时间: {{ formatDate(image.createTime) }}</p>
          <div class="tag-list">
            <span class="tag" v-for="tag in image.tags" :key="tag">{{ tag }}</span>
          </div>
        </div>
        <div class="review-actions">
          <button class="approve-btn" @click="handleApprove(image)" :disabled="image.status !== 'pending'">通过</button>
          <button class="reject-btn" @click="handleReject(image)" :disabled="image.status !== 'pending'">拒绝</button>
        </div>
      </div>
    </div>
    
    <div class="pagination" v-if="total > 0">
      <button :disabled="currentPage === 1" @click="currentPage--; fetchImages()">上一页</button>
      <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
      <button :disabled="currentPage >= totalPages" @click="currentPage++; fetchImages()">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Toast from '../components/Toast.vue'

const router = useRouter()
const images = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const status = ref('pending')
const search = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const showMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => showToast.value = false, 3000)
}

const fetchImages = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      router.push('/login')
      return
    }

    const params = new URLSearchParams()
    params.append('page', currentPage.value)
    params.append('size', pageSize.value)
    params.append('status', status.value)
    if (search.value) params.append('search', search.value)

    const res = await fetch(`/admin/images/review?${params.toString()}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (!res.ok) throw new Error('获取图片列表失败')

    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      images.value = data.data.records || []
      total.value = data.data.total || 0
    } else {
      throw new Error(data.message || '获取图片列表失败')
    }
  } catch (error) {
    console.error('Error fetching images:', error)
    showMessage(error.message || '获取图片失败', 'error')
    images.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleApprove = async (image) => {
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`/admin/images/${image.id}/approve`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ approved: true })
    })

    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      image.status = 'approved'
      showMessage('审核通过成功')
    } else {
      throw new Error(data.message || '审核操作失败')
    }
  } catch (error) {
    console.error('Error approving image:', error)
    showMessage(error.message || '审核失败', 'error')
  }
}

const handleReject = async (image) => {
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`/admin/images/${image.id}/approve`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ approved: false })
    })

    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      image.status = 'rejected'
      showMessage('已拒绝该图片')
    } else {
      throw new Error(data.message || '审核操作失败')
    }
  } catch (error) {
    console.error('Error rejecting image:', error)
    showMessage(error.message || '操作失败', 'error')
  }
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  fetchImages()
})
</script>

<style scoped>
.admin-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.back-btn {
  padding: 8px 16px;
  background-color: #666;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.review-filters {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.review-filters select {
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.search-box {
  display: flex;
  flex: 1;
  min-width: 200px;
}

.search-box input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
}

.search-box button {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.image-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.image-card:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.review-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.image-info {
  padding: 15px;
}

.image-info h3 {
  margin: 0 0 10px;
  font-size: 1.1rem;
}

.image-info p {
  margin: 5px 0;
  color: #666;
  font-size: 0.9rem;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 10px;
}

.tag {
  background-color: #f0f0f0;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
}

.review-actions {
  display: flex;
  padding: 10px 15px;
  background-color: #f9f9f9;
  border-top: 1px solid #eee;
}

.approve-btn {
  flex: 1;
  padding: 8px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
}

.reject-btn {
  flex: 1;
  padding: 8px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 5px;
}

.approve-btn:disabled, .reject-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

.pagination button {
  padding: 8px 16px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.no-results {
  text-align: center;
  padding: 50px;
  color: #666;
}
</style>