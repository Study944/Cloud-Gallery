<template>
  <div id="app">
    <!-- 导航栏优化 -->
    <header class="navbar">
      <div class="logo">
        <span class="logo-icon">🖼️</span>
        <span class="logo-text">Cloud Gallery</span>
      </div>
      
      <div class="search-bar">
        <input 
          v-model="search" 
          placeholder="搜索图片素材" 
          @keyup.enter="handleSearch" 
        />
        <button class="search-btn" @click="handleSearch">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
        </button>
      </div>
      
      <div class="nav-actions">
        <template v-if="!isLogin">
          <button class="nav-btn login-btn" @click="showLogin = true">登录</button>
          <button class="nav-btn register-btn" @click="showRegister = true">注册</button>
        </template>
        <template v-else>
          <button class="upload-btn" @click="showUpload = true">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="17 8 12 3 7 8"></polyline>
              <line x1="12" y1="3" x2="12" y2="15"></line>
            </svg>
            <span>上传</span>
          </button>
          
          <div class="user-dropdown">
            <button class="nav-btn user-btn">
              <span class="user-avatar">{{ userData && userData.username ? userData.username.charAt(0).toUpperCase() : 'U' }}</span>
              <span class="user-name">{{ userData && userData.username ? userData.username : '用户' }}</span>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="6 9 12 15 18 9"></polyline>
              </svg>
            </button>
            <div class="dropdown-content">
              <a href="#" @click.prevent="showUserInfo = true">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                个人信息
              </a>
              <a href="#" @click.prevent="logout">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                  <polyline points="16 17 21 12 16 7"></polyline>
                  <line x1="21" y1="12" x2="9" y2="12"></line>
                </svg>
                退出登录
              </a>
            </div>
          </div>
        </template>
      </div>
    </header>
    
    <!-- 主内容区优化 -->
    <main class="gallery-main" ref="galleryMainRef">
      <div class="hero-section">
        <h1>探索高质量图片素材的世界</h1>
        <p class="gallery-desc">Cloud Gallery拥有超过5.5百万张优质图片和视频素材</p>
        <div class="hero-search">
          <input 
            v-model="heroSearch" 
            placeholder="搜索图片、插画、摄影作品..." 
            @keyup.enter="handleHeroSearch" 
          />
          <button @click="handleHeroSearch">搜索</button>
        </div>
      </div>
      
      <div class="gallery-filters" v-if="images.length > 0">
        <div class="filter-title">探索图片</div>
        <div class="filter-tags">
          <button class="filter-tag" @click="handleTagSearch('风景')">风景</button>
          <button class="filter-tag" @click="handleTagSearch('人物')">人物</button>
          <button class="filter-tag" @click="handleTagSearch('动物')">动物</button>
          <button class="filter-tag" @click="handleTagSearch('建筑')">建筑</button>
          <button class="filter-tag" @click="handleTagSearch('科技')">科技</button>
        </div>
      </div>
      
      <ImageGallery :images="images" @image-click="handleImageClick" @download="handleImageDownload" />
      
      <div v-if="loading" class="loading-spinner">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="images.length === 0 && !loading" class="no-results">
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
          <rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18"></rect>
          <line x1="7" y1="2" x2="7" y2="22"></line>
          <line x1="17" y1="2" x2="17" y2="22"></line>
          <line x1="2" y1="12" x2="22" y2="12"></line>
          <line x1="2" y1="7" x2="7" y2="7"></line>
          <line x1="2" y1="17" x2="7" y2="17"></line>
          <line x1="17" y1="17" x2="22" y2="17"></line>
          <line x1="17" y1="7" x2="22" y2="7"></line>
        </svg>
        <p>暂无图片，请尝试其他搜索词或上传新图片</p>
      </div>
      
      <div v-else class="pagination-bar">
        <button :disabled="current===1" @click="current--;fetchImages(search.value)">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
          上一页
        </button>
        <span>第 {{current}} 页</span>
        <button :disabled="images.length<pageSize" @click="current++;fetchImages(search.value)">
          下一页
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </button>
      </div>
    </main>
    
    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于 Cloud Gallery</h3>
          <p>Cloud Gallery 是一个高质量图片素材分享平台，为设计师和创作者提供免费优质的图片资源。</p>
        </div>
        <div class="footer-section">
          <h3>快速链接</h3>
          <ul>
            <li><a href="#">首页</a></li>
            <li><a href="#">热门图片</a></li>
            <li><a href="#">最新上传</a></li>
            <li><a href="#">关于我们</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h3>联系我们</h3>
          <p>邮箱：contact@cloudgallery.com</p>
          <div class="social-icons">
            <a href="#" class="social-icon">微博</a>
            <a href="#" class="social-icon">微信</a>
            <a href="#" class="social-icon">QQ</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2023 Cloud Gallery. 保留所有权利。</p>
      </div>
    </footer>
    
    <!-- 弹窗区 -->
    <UserLogin v-if="showLogin" @close="showLogin = false" @login-success="handleLoginSuccess" />
    <UserRegister v-if="showRegister" @close="showRegister = false" @register-success="showLogin = true; showRegister = false" />
    <UserInfo v-if="showUserInfo" @close="showUserInfo = false" />
    <ImageUpload v-if="showUpload" @close="showUpload = false" @upload-success="handleUploadSuccess" />
    <!-- 新增Toast组件 -->
    <Toast v-if="showToast" :message="toastMessage" :type="toastType" :duration="3000" @close="showToast = false" />
  </div>
</template>

<script setup>
// 保留原有的脚本逻辑，添加新功能
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import UserLogin from '../components/UserLogin.vue'
import UserRegister from '../components/UserRegister.vue'
import UserInfo from '../components/UserInfo.vue'
import ImageGallery from '../components/ImageGallery.vue'
import ImageUpload from '../components/ImageUpload.vue'
import Toast from '../components/Toast.vue'

const router = useRouter()
const showLogin = ref(false)
const showRegister = ref(false)
const showUserInfo = ref(false)
const showUpload = ref(false)
const search = ref('')
const heroSearch = ref('') // 新增大搜索框的值
const isLogin = computed(() => !!localStorage.getItem('user'))
const userData = ref(null)
const galleryMainRef = ref(null)
const images = ref([])
const current = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false)

// 更新用户数据的方法
const updateUserData = () => {
  const user = localStorage.getItem('user')
  userData.value = user ? JSON.parse(user) : null
}

// 退出登录
const logout = () => {
  localStorage.removeItem('user')
  updateUserData()
  window.location.reload()
}

// 获取图片列表（支持搜索）
const fetchImages = async (searchName = '') => {
  loading.value = true
  try {
    const body = {
      current: current.value,
      pageSize: pageSize.value,
    }
    if (searchName) {
      body.name = searchName
    }
    // 动态构建请求头
    const headers = {
      'Content-Type': 'application/json'
    }
    const token = localStorage.getItem('token')
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    const res = await fetch('/image/list/page/vo', {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(body)
    })
    const data = await res.json()
    if ((data.code === 0 || data.code === 20000) && data.data) {
      const processedRecords = data.data.records.map(img => ({
        ...img,
        url: img.url.replace(/`/g, '').trim()
      }))
      images.value = processedRecords
      total.value = data.data.total
    } else {
      images.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取图片失败', error)
    images.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  current.value = 1
  fetchImages(search.value.trim())
}

// 大搜索框搜索处理
const handleHeroSearch = () => {
  if (!heroSearch.value.trim()) return
  search.value = heroSearch.value
  current.value = 1
  fetchImages(heroSearch.value.trim())
  setTimeout(() => {
    const galleryElement = document.querySelector('.masonry')
    if (galleryElement) {
      galleryElement.scrollIntoView({ behavior: 'smooth' })
    }
  }, 100)
}

// 标签搜索
const handleTagSearch = (tag) => {
  search.value = tag
  heroSearch.value = tag
  current.value = 1
  fetchImages(tag)
}

// 登录成功处理
const handleLoginSuccess = (user) => {
  showLogin.value = false
  updateUserData()
  window.location.reload() // 登录成功后刷新页面
}

// 上传成功处理
const handleUploadSuccess = () => {
  showUpload.value = false // 关闭上传窗口
  current.value = 1
  fetchImages(search.value.trim()) // 刷新图片列表并重置到第一页
}

// 图片点击处理
const handleImageClick = (img) => {
  router.push({ name: 'ImageDetail', params: { id: img.id } })
}

// 新增Toast相关状态
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

// 显示提示消息
const showMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

// 下载图片
const handleImageDownload = async (img) => {
  if (!img || !img.id) {
    showMessage('图片信息不完整，无法下载', 'error');
    return;
  }

  try {
    const token = localStorage.getItem('token');
    const headers = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${import.meta.env.VITE_API_URL}/download?id=${img.id}`, {
      method: 'GET',
      headers: headers
    });

    // 打印所有响应头用于调试
    console.log('下载响应头:', Array.from(response.headers.entries()));

    // 检查响应头
    const contentType = response.headers.get('Content-Type');
    const contentDisposition = response.headers.get('Content-Disposition');

    // 调试信息
    console.log('Content-Type:', contentType);
    console.log('Content-Disposition:', contentDisposition);

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`下载失败: ${response.status} ${response.statusText}\n响应内容: ${errorText}`);
    }

    // 如果没有Content-Type头，尝试从URL推断
    let mimeType = contentType;
    if (!mimeType) {
      const urlParts = img.url.split('?')[0].split('.');
      const extension = urlParts.length > 1 ? urlParts.pop().toLowerCase() : 'jpg';
      // 简单的MIME类型映射
      const mimeMap = {
        'jpg': 'image/jpeg',
        'jpeg': 'image/jpeg',
        'png': 'image/png',
        'gif': 'image/gif',
        'bmp': 'image/bmp',
        'webp': 'image/webp',
        'svg': 'image/svg+xml'
      };
      mimeType = mimeMap[extension] || 'application/octet-stream';
    }

    const blob = await response.blob();
    
    // 检查blob大小，过小可能表示下载失败
    if (blob.size < 100) {
      throw new Error(`下载的文件过小，可能损坏 (${blob.size} bytes)`);
    }
    
    // 使用正确的MIME类型创建blob
    const typedBlob = new Blob([blob], { type: mimeType });
    const url = window.URL.createObjectURL(typedBlob);
    const a = document.createElement('a');
    a.href = url;
    
    // 优先从响应头获取文件名
    let fileName = '';
    if (contentDisposition && contentDisposition.includes('filename=')) {
      // 处理可能的引号包裹的文件名
      const filenameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
      if (filenameMatch && filenameMatch[1]) {
        fileName = decodeURIComponent(filenameMatch[1]);
      }
    }
    
    // 如果响应头中没有文件名或解析失败，从URL和img信息生成
    if (!fileName) {
      const urlParts = img.url.split('?')[0].split('.');
      const extension = urlParts.length > 1 ? urlParts.pop().toLowerCase() : 'jpg';
      const baseName = img.name || '未命名图片';
      // 确保文件名不包含特殊字符且有正确扩展名
      fileName = `${baseName.replace(/[^a-zA-Z0-9_.-]/g, '_')}.${extension}`;
    }
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    showMessage('下载成功')
  } catch (error) {
    console.error('下载失败', error)
    showMessage(error.message || '下载失败，请重试', 'error')
  }
}

// 收藏图片
const handleImageLike = async (img) => {
  try {
    const res = await fetch('/image/like', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      },
      body: JSON.stringify({ imageId: img.id, liked: img.liked })
    })

    const data = await res.json()
    if (data.code !== 0 && data.code !== 20000) throw new Error(data.message || '操作失败')

    showMessage(img.liked ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    console.error('收藏操作失败', error)
    img.liked = !img.liked // 回滚状态
    showMessage(error.message || '操作失败，请重试', 'error')
  }
}

onMounted(async () => {
  try {
    const res = await fetch('/user/getLoginUser', { credentials: 'include' })
    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      localStorage.setItem('user', JSON.stringify(data.data))
    } else {
      localStorage.removeItem('user')
    }
  } catch (e) {
    localStorage.removeItem('user')
  }
  updateUserData()
  // 确保首次加载调用图片接口
  fetchImages()
})
</script>

<style scoped>
/* 导航栏样式优化 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background-color: var(--bg-white);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  font-size: 22px;
  color: var(--primary-color);
}

.logo-icon {
  font-size: 24px;
}

.search-bar {
  flex: 1;
  max-width: 600px;
  margin: 0 24px;
  position: relative;
}

.search-bar input {
  width: 100%;
  padding: 10px 16px 10px 40px;
  border-radius: 24px;
  border: 1px solid var(--border-color);
  background-color: var(--bg-light);
  font-size: 14px;
  transition: all 0.3s;
}

.search-bar input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 153, 229, 0.1);
}

.search-btn {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-btn {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
}

.login-btn {
  background: transparent;
  color: var(--text-primary);
}

.login-btn:hover {
  background-color: var(--bg-light);
}

.register-btn {
  background-color: var(--primary-color);
  color: white;
}

.register-btn:hover {
  background-color: var(--primary-dark);
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: var(--primary-color);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-btn:hover {
  background-color: var(--primary-dark);
}

/* 主内容区样式优化 */
.gallery-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-section {
  text-align: center;
  padding: 60px 20px;
  margin-bottom: 30px;
}

.hero-section h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.gallery-desc {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 32px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-search {
  display: flex;
  max-width: 700px;
  margin: 0 auto;
}

.hero-search input {
  flex: 1;
  padding: 14px 20px;
  border: 1px solid var(--border-color);
  border-radius: 8px 0 0 8px;
  font-size: 16px;
}

.hero-search button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 0 24px;
  border-radius: 0 8px 8px 0;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.hero-search button:hover {
  background-color: var(--primary-dark);
}

.gallery-filters {
  margin-bottom: 30px;
}

.filter-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-tag {
  padding: 6px 16px;
  background-color: var(--bg-white);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tag:hover {
  background-color: var(--primary-light);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .navbar {
    padding: 10px 16px;
  }

  .search-bar {
    display: none;
  }

  .hero-section {
    padding: 40px 16px;
  }

  .hero-section h1 {
    font-size: 32px;
  }

  .gallery-desc {
    font-size: 16px;
  }
}

.user-dropdown {
  position: relative;
  display: inline-block;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.dropdown-content {
  display: none;
  position: absolute;
  right: 0;
  top: 100%;
  margin-top: 8px;
  background-color: var(--bg-white);
  min-width: 180px;
  box-shadow: var(--shadow-md);
  border-radius: var(--radius-md);
  overflow: hidden;
  z-index: 101;
}

.dropdown-content a {
  color: var(--text-primary);
  padding: 12px 16px;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background 0.2s;
}

.dropdown-content a:hover {
  background-color: var(--bg-light);
  color: var(--primary-color);
}

.user-dropdown:hover .dropdown-content {
  display: block;
}

/* 主内容区样式 */
.gallery-main {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 16px;
  width: 100%;
}

/* 英雄区域 */
.hero-section {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  border-radius: var(--radius-lg);
  margin: 32px 0;
}

.hero-section h1 {
  font-size: 2.5rem;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.gallery-desc {
  font-size: 1.2rem;
  color: var(--text-secondary);
  margin-bottom: 32px;
}

.hero-search {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  position: relative;
}

.hero-search input {
  flex: 1;
  padding: 16px 20px;
  font-size: 1.1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  outline: none;
  box-shadow: var(--shadow-sm);
  transition: all 0.2s;
}

.hero-search input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 153, 229, 0.1);
}

.hero-search button {
  position: absolute;
  right: 4px;
  top: 4px;
  bottom: 4px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  padding: 0 24px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.hero-search button:hover {
  background: var(--primary-dark);
}

/* 筛选区域 */
.gallery-filters {
  margin: 32px 0;
}

.filter-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}

.filter-tag {
  background: var(--bg-white);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  font-size: 1rem;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tag:hover {
  background: var(--primary-light);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

/* 加载状态 */
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: var(--text-tertiary);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(0, 153, 229, 0.2);
  border-radius: 50%;
  border-top-color: var(--primary-color);
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 无结果状态 */
.no-results {
  text-align: center;
  padding: 60px 0;
  color: var(--text-tertiary);
}

.no-results svg {
  margin-bottom: 16px;
  color: var(--text-tertiary);
}

.no-results p {
  font-size: 1.1rem;
}

/* 分页栏 */
.pagination-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin: 40px 0;
  padding: 16px 0;
}

.pagination-bar button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--bg-white);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 10px 20px;
  font-size: 1rem;
  cursor: pointer;
  color: var(--text-primary);
  transition: all 0.2s;
}

.pagination-bar button:hover:not(:disabled) {
  background: var(--primary-light);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.pagination-bar button:disabled {
  color: var(--text-tertiary);
  cursor: not-allowed;
  background: var(--bg-light);
}

/* 页脚 */
.footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 0 0;
  margin-top: 60px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 20px;
}

.footer-section {
  flex: 1;
  min-width: 250px;
  margin-bottom: 30px;
  padding: 0 15px;
}

.footer-section h3 {
  font-size: 1.2rem;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
}

.footer-section h3:after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 40px;
  height: 2px;
  background: var(--primary-color);
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-section a:hover {
  color: var(--primary-color);
}

.social-icons {
  display: flex;
  gap: 15px;
  margin-top: 15px;
}

.social-icon {
  color: #ecf0f1;
  text-decoration: none;
  transition: color 0.2s;
}

.social-icon:hover {
  color: var(--primary-color);
}

.footer-bottom {
  background: #1a252f;
  padding: 20px 0;
  text-align: center;
  margin-top: 20px;
}

.footer-bottom p {
  margin: 0;
  font-size: 0.9rem;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .navbar {
    padding: 0 16px;
  }
  
  .search-bar {
    margin: 0 12px;
    max-width: 200px;
  }
  
  .logo-text {
    font-size: 1.5rem;
  }
  
  .hero-section {
    padding: 40px 16px;
  }
  
  .hero-section h1 {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .navbar {
    height: auto;
    padding: 12px 16px;
    flex-wrap: wrap;
  }
  
  .logo {
    order: 1;
    margin-right: auto;
  }
  
  .nav-actions {
    order: 2;
  }
  
  .search-bar {
    order: 3;
    margin: 12px 0 0 0;
    max-width: 100%;
    width: 100%;
  }
  
  .hero-section {
    padding: 30px 16px;
  }
  
  .hero-section h1 {
    font-size: 1.8rem;
  }
  
  .gallery-desc {
    font-size: 1rem;
  }
  
  .footer-content {
    flex-direction: column;
  }
  
  .footer-section {
    margin-bottom: 20px;
  }
}

@media (max-width: 576px) {
  .nav-btn {
    padding: 6px 12px;
    font-size: 0.9rem;
  }
  
  .upload-btn {
    padding: 6px 12px;
  }
  
  .hero-search button {
    position: relative;
    right: auto;
    top: auto;
    bottom: auto;
    margin-top: 10px;
    width: 100%;
    padding: 12px;
  }
  
  .hero-search {
    flex-direction: column;
  }
  
  .pagination-bar button {
    padding: 8px 12px;
  }
}
</style>

<ImageGallery 
  :images="images" 
  @image-click="handleImageClick" 
  @download="handleImageDownload" 
  @like="handleImageLike" 
/>