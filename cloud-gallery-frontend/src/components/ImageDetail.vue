<template>
  <div>
    <header class="navbar">
      <div class="logo" @click="$router.push('/')" style="cursor:pointer">Cloud Gallery</div>
      <div class="nav-actions">
        <template v-if="!isLogin">
          <button class="nav-btn" @click="goLogin">登录</button>
          <button class="nav-btn" @click="goRegister">加入</button>
        </template>
        <template v-else>
          <div class="user-dropdown">
            <button class="nav-btn user-btn">{{ userData && userData.username ? userData.username : '用户' }}</button>
            <div class="dropdown-content">
              <a href="#" @click.prevent="showUserInfo = true">个人信息</a>
              <a href="#" @click.prevent="logout">退出登录</a>
            </div>
          </div>
        </template>
      </div>
    </header>
    <div class="image-detail-bg">
      <div class="image-detail-main">
        <div class="image-detail-left">
          <img :src="image.url" :alt="image.name" class="main-img" />
          <div class="img-title">{{ image.name }}</div>
          <div class="img-desc">{{ image.description }}</div>
          <div class="img-labels" v-if="image.label">
            <span class="img-label">{{ image.label }}</span>
          </div>
          <button class="back-btn" @click="goBack">返回</button>
        </div>
        <div class="image-detail-right">
          <button class="download-btn" @click="downloadImage">下载图片</button>
          <div class="info-block">
            <div><b>ID：</b>{{ image.id }}</div>
            <div><b>作者：</b>{{ image.userId }}</div>
            <div><b>尺寸：</b>{{ image.width }} x {{ image.height }}</div>
            <div><b>大小：</b>{{ (image.size/1024/1024).toFixed(2) }} MB</div>
            <div><b>上传时间：</b>{{ image.createTime }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
const imageId = route.params.id
const image = ref({})
const showUserInfo = ref(false)
const isLogin = computed(() => !!localStorage.getItem('user'))
const userData = ref(null)

const updateUserData = () => {
  const user = localStorage.getItem('user')
  userData.value = user ? JSON.parse(user) : null
}

const logout = () => {
  localStorage.removeItem('user')
  updateUserData()
  window.location.reload()
}

const goLogin = () => {
  router.push('/')
  setTimeout(() => window.scrollTo(0, 0), 100)
}
const goRegister = () => {
  router.push('/')
  setTimeout(() => window.scrollTo(0, 0), 100)
}

const fetchDetail = async () => {
  const res = await fetch(`/image/getById?id=${imageId}`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' }
  })
  const data = await res.json()
  if (data.code === 0 || data.code === 20000) {
    image.value = data.data
  }
}

const downloadImage = () => {
  const a = document.createElement('a')
  a.href = image.value.url
  a.download = image.value.name || 'image'
  a.target = '_blank'
  a.click()
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  updateUserData()
  fetchDetail()
})
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 64px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  position: sticky;
  top: 0;
  z-index: 10;
}
.logo {
  font-size: 2rem;
  font-weight: bold;
  color: #222;
  letter-spacing: 1px;
  font-family: 'Segoe UI', Arial, sans-serif;
}
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nav-btn {
  background: none;
  border: none;
  color: #222;
  font-size: 1rem;
  margin: 0 4px;
  cursor: pointer;
  padding: 6px 14px;
  border-radius: 6px;
  transition: background 0.2s;
}
.nav-btn:hover {
  background: #f2f3f5;
}
.user-dropdown {
  position: relative;
  display: inline-block;
}
.dropdown-content {
  display: none;
  position: absolute;
  background-color: #fff;
  min-width: 160px;
  box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
  z-index: 1;
  border-radius: 8px;
  overflow: hidden;
}
.dropdown-content a {
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
  text-align: left;
}
.dropdown-content a:hover {background-color: #f1f1f1}
.user-dropdown:hover .dropdown-content {
  display: block;
}
.image-detail-bg {
  background: #fafafa;
  min-height: 100vh;
  padding: 40px 0;
  display: flex;
  justify-content: center;
}
.image-detail-main {
  display: flex;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  max-width: 1100px;
  width: 100%;
  padding: 32px 40px;
  gap: 48px;
}
.image-detail-left {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.main-img {
  max-width: 420px;
  max-height: 520px;
  border-radius: 12px;
  margin-bottom: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
}
.img-title {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 8px;
}
.img-desc {
  color: #555;
  margin-bottom: 8px;
}
.img-labels {
  margin-bottom: 8px;
}
.img-label {
  background: #0099e5;
  color: #fff;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 0.95rem;
  margin-right: 4px;
}
.image-detail-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 24px;
}
.download-btn {
  background: #0099e5;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 32px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  margin-bottom: 18px;
  transition: background 0.2s;
}
.download-btn:hover {
  background: #007acc;
}
.info-block {
  font-size: 1rem;
  color: #333;
  line-height: 2;
}
.back-btn {
  margin-top: 18px;
  background: #eee;
  border: none;
  border-radius: 6px;
  padding: 8px 24px;
  font-size: 1rem;
  cursor: pointer;
  color: #333;
  transition: background 0.2s;
}
.back-btn:hover {
  background: #ddd;
}
</style> 