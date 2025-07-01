<template>
  <div class="user-info-modal-bg">
    <div class="user-info-modal">
      <button class="close-btn" @click="$emit('close')">×</button>
      <h2>个人信息</h2>
      <div v-if="loading" class="loading-spinner">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>
      <div v-else-if="user">
        <img v-if="user.avatar" :src="user.avatar" class="avatar" :alt="user.username + '的头像'" />
        <div v-else class="default-avatar">{{ user.username.charAt(0).toUpperCase() }}</div>
        <div class="info-row"><b>用户名：</b>{{ user.username || '未设置' }}</div>
        <div class="info-row"><b>账号：</b>{{ user.account || '未知' }}</div>
        <div class="info-row"><b>角色：</b>{{ user.role || '普通用户' }}</div>
        <div class="info-row"><b>注册时间：</b>{{ formatDate(user.createTime) || '未知时间' }}</div>
      </div>
      <div v-else class="error-state">
        <p>无法加载用户信息</p>
        <button class="retry-btn" @click="fetchUser">重试</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Toast from './Toast.vue'

const user = ref(null)
const loading = ref(true)
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('error')

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

const showMessage = (message, type = 'error') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

const fetchUser = async () => {
  try {
    loading.value = true
    const token = localStorage.getItem('token')
    const headers = {}
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    const res = await fetch('/user/getLoginUser', {
      method: 'GET',
      headers: headers,
      credentials: 'include'
    })

    if (!res.ok) throw new Error(`请求失败: ${res.status} ${res.statusText}`)

    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      user.value = data.data
    } else {
      user.value = null
      showMessage(data.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
    user.value = null
    showMessage(error.message || '网络错误，无法获取用户信息')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUser()
})
</script>

<style scoped>
.user-info-modal-bg {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.user-info-modal {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  padding: 32px 36px 24px 36px;
  min-width: 320px;
  position: relative;
  text-align: center;
}
.close-btn {
  position: absolute;
  right: 18px;
  top: 12px;
  background: none;
  border: none;
  font-size: 1.6rem;
  cursor: pointer;
  color: #888;
}
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 18px;
  border: 2px solid #eee;
}
.info-row {
  margin: 10px 0;
  font-size: 1.05rem;
  color: #333;
}
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
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
.default-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #3498db;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin: 0 auto 18px;
  border: 2px solid #eee;
}
.error-state {
  text-align: center;
  padding: 20px;
}
.retry-btn {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.retry-btn:hover {
  background-color: #2980b9;
}
</style>