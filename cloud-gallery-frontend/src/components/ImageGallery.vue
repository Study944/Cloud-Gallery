<template>
  <div class="masonry">
    <div 
      v-for="img in images" 
      :key="img.id" 
      class="img-card" 
      @click="handleImageClick(img)"
    >
      <div class="img-wrap">
        <div class="img-placeholder" v-if="!img.loaded && !img.error">
          <div class="img-skeleton"></div>
        </div>
        <img 
          :src="img.url" 
          :alt="img.name" 
          loading="lazy" 
          @load="img.loaded = true" 
          @error="handleImageError(img)" 
          v-if="!img.error"
        />
        <div class="img-error" v-if="img.error">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
        </div>
        <div class="img-overlay">
          <div class="img-info">
            <h3 class="img-name">{{ img.name || '未命名图片' }}</h3>
            <p class="img-desc" v-if="img.description">{{ img.description }}</p>
            <div class="img-labels" v-if="img.label">
              <span class="img-label">{{ img.label }}</span>
            </div>
          </div>
          <div class="img-actions">
            <button class="img-action-btn download-btn" title="下载" @click.stop="handleDownload(img)">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
              </svg>
            </button>
            <button class="img-action-btn like-btn" title="收藏" @click.stop="handleLike(img)">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 图片预览模态框 -->
  <div class="preview-modal" v-if="previewImage" @click="closePreview">
    <div class="preview-content" @click.stop>
      <button class="close-btn" @click="closePreview">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
      <img :src="previewImage.url" :alt="previewImage.name" class="preview-img" />
      <div class="preview-info">
        <h2>{{ previewImage.name || '未命名图片' }}</h2>
        <p v-if="previewImage.description">{{ previewImage.description }}</p>
        <div class="preview-meta">
          <span>上传时间: {{ formatDate(previewImage.createTime) }}</span>
          <span>尺寸: {{ previewImage.width }} × {{ previewImage.height }}</span>
          <span>大小: {{ formatSize(previewImage.size) }}</span>
        </div>
        <div class="preview-actions">
          <button class="btn download-btn" @click="handleDownload(previewImage)">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            下载图片
          </button>
          <button class="btn like-btn" @click="handleLike(previewImage)">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
            {{ previewImage.liked ? '取消收藏' : '收藏' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const props = defineProps({
  images: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['image-click', 'download', 'like'])
const previewImage = ref(null)

// 初始化图片加载状态
for (const img of props.images) {
  if (!('loaded' in img)) img.loaded = false
  if (!('error' in img)) img.error = false
  if (!('liked' in img)) img.liked = false
}

const handleImageClick = (img) => {
  previewImage.value = img
}

const closePreview = () => {
  previewImage.value = null
}

const handleImageError = (img) => {
  img.error = true
}

const handleDownload = (img) => {
  emit('download', img)
  // 这里可以添加下载成功的提示
}

const handleLike = (img) => {
  img.liked = !img.liked
  emit('like', img)
}

const formatDate = (dateString) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  return date.toLocaleString()
}

const formatSize = (size) => {
  if (!size) return '未知大小'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  return `${(size / (1024 * 1024)).toFixed(1)} MB`
}
</script>

<style scoped>
.masonry {
  column-count: 4;
  column-gap: 20px;
  width: 100%;
}

@media (max-width: 1200px) {
  .masonry { column-count: 3; }
}

@media (max-width: 800px) {
  .masonry { column-count: 2; }
}

@media (max-width: 500px) {
  .masonry { column-count: 1; }
}

.img-card {
  break-inside: avoid;
  margin-bottom: 20px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  background: var(--bg-white);
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
  cursor: pointer;
}

.img-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
}

.img-wrap {
  position: relative;
  width: 100%;
  display: block;
  overflow: hidden;
}

.img-wrap img {
  width: 100%;
  display: block;
  transition: transform 0.5s;
}

.img-card:hover img {
  transform: scale(1.05);
}

.img-overlay {
  position: absolute;
  left: 0; right: 0; bottom: 0;
  background: linear-gradient(0deg, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0) 100%);
  padding: 20px 16px 16px 16px;
  color: white;
  opacity: 0;
  transform: translateY(10px);
  transition: opacity 0.3s, transform 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.img-card:hover .img-overlay {
  opacity: 1;
  transform: translateY(0);
}

.img-info {
  flex: 1;
}

.img-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 4px 0;
  text-shadow: 0 1px 3px rgba(0,0,0,0.3);
}

.img-desc {
  font-size: 0.9rem;
  margin: 0 0 8px 0;
  opacity: 0.9;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.img-labels {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.img-label {
  background: rgba(0, 153, 229, 0.8);
  color: white;
  border-radius: 4px;
  padding: 2px 8px;
  font-size: 0.8rem;
  font-weight: 500;
}

.img-actions {
  display: flex;
  gap: 8px;
}

.img-action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.img-action-btn:hover {
  background: rgba(255, 255, 255, 0.4);
}

.download-btn:hover {
  background: var(--success-color);
}

.like-btn:hover {
  background: var(--error-color);
}

/* 新增图片加载状态样式 */
.img-placeholder {
  width: 100%;
  background: #f0f0f0;
  position: relative;
  overflow: hidden;
}

.img-skeleton {
  width: 100%;
  padding-top: 100%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.img-error {
  width: 100%;
  padding: 40px 0;
  background: #f8f8f8;
  color: #ff4d4f;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 图片预览模态框样式 */
.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.preview-content {
  max-width: 90%;
  max-height: 90%;
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
}

.preview-img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  margin: 0 auto;
}

.preview-info {
  padding: 20px;
}

.preview-meta {
  display: flex;
  gap: 16px;
  margin: 16px 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.preview-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border-radius: var(--radius-md);
  border: none;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.download-btn {
  background: var(--primary-color);
  color: white;
}

.like-btn {
  background: var(--error-color);
  color: white;
}
</style>