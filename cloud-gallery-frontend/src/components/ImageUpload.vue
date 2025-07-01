<template>
  <div class="upload-modal-bg">
    <div class="upload-modal">
      <button class="close-btn" @click="$emit('close')">×</button>
      <h2>上传图片</h2>
      
      <div class="upload-tabs">
        <button 
          :class="['tab-btn', activeTab === 'file' ? 'active' : '']" 
          @click="activeTab = 'file'"
        >本地文件</button>
        <button 
          :class="['tab-btn', activeTab === 'url' ? 'active' : '']" 
          @click="activeTab = 'url'"
        >图片URL</button>
      </div>
      
      <div class="upload-content">
        <!-- 文件上传 -->
        <div v-if="activeTab === 'file'" class="file-upload">
          <div 
            class="drop-area" 
            @dragover.prevent 
            @drop.prevent="handleFileDrop"
            @click="triggerFileInput"
          >
            <input 
              type="file" 
              ref="fileInput" 
              style="display: none" 
              accept="image/*"
              @change="handleFileChange"
            >
            <div v-if="!selectedFile">
              <div class="upload-icon">📁</div>
              <p>点击或拖拽图片到此处</p>
            </div>
            <div v-else class="preview">
              <img :src="previewUrl" alt="预览图片">
              <p>{{ selectedFile.name }}</p>
            </div>
          </div>
          
          <div class="form-group">
            <label>图片名称</label>
            <input v-model="uploadData.name" type="text" placeholder="请输入图片名称">
          </div>
          
          <div class="form-group">
            <label>图片描述</label>
            <textarea v-model="uploadData.description" placeholder="请输入图片描述"></textarea>
          </div>
          
          <div class="form-group">
            <label>图片标签</label>
            <input v-model="uploadData.label" type="text" placeholder="请输入图片标签">
          </div>
          
          <button 
            class="upload-submit-btn" 
            @click="uploadFile"
            :disabled="!selectedFile || uploading"
          >
            {{ uploading ? '上传中...' : '上传图片' }}
          </button>
        </div>
        
        <!-- URL上传 -->
        <div v-if="activeTab === 'url'" class="url-upload">
          <div class="form-group">
            <label>图片URL</label>
            <input 
              v-model="uploadData.url" 
              type="text" 
              placeholder="请输入图片URL（以http://或https://开头）"
            >
          </div>
          
          <div class="form-group">
            <label>图片名称</label>
            <input v-model="uploadData.name" type="text" placeholder="请输入图片名称">
          </div>
          
          <div class="form-group">
            <label>图片描述</label>
            <textarea v-model="uploadData.description" placeholder="请输入图片描述"></textarea>
          </div>
          
          <div class="form-group">
            <label>图片标签</label>
            <input v-model="uploadData.label" type="text" placeholder="请输入图片标签">
          </div>
          
          <button 
            class="upload-submit-btn" 
            @click="uploadUrl"
            :disabled="!uploadData.url || uploading"
          >
            {{ uploading ? '上传中...' : '上传图片' }}
          </button>
        </div>
      </div>
      
      <div v-if="error" class="error">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const emit = defineEmits(['close', 'upload-success'])

const activeTab = ref('file')
const selectedFile = ref(null)
const previewUrl = ref('')
const fileInput = ref(null)
const error = ref('')
const uploading = ref(false)

const uploadData = reactive({
  name: '',
  url: '',
  description: '',
  label: ''
})

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
    // 自动填充文件名
    if (!uploadData.name) {
      uploadData.name = file.name
    }
  }
}

const handleFileDrop = (event) => {
  const file = event.dataTransfer.files[0]
  if (file && file.type.startsWith('image/')) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
    // 自动填充文件名
    if (!uploadData.name) {
      uploadData.name = file.name
    }
  } else {
    error.value = '请上传图片文件'
  }
}

const uploadFile = async () => {
  if (!selectedFile.value) {
    error.value = '请选择要上传的图片'
    return
  }
  
  error.value = ''
  uploading.value = true
  
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    
    if (uploadData.name) {
      formData.append('name', uploadData.name)
    }
    if (uploadData.description) {
      formData.append('description', uploadData.description)
    }
    if (uploadData.label) {
      formData.append('label', uploadData.label)
    }
    
    const res = await fetch('/image/upload/file', {
      method: 'POST',
      body: formData
    })
    
    const data = await res.json()
    if (data.code === 0) {
      emit('upload-success', data.data)
      emit('close')
    } else {
      error.value = data.message || '上传失败'
    }
  } catch (e) {
    error.value = '网络错误'
    console.error(e)
  } finally {
    uploading.value = false
  }
}

const uploadUrl = async () => {
  if (!uploadData.url) {
    error.value = '请输入图片URL'
    return
  }
  
  if (!uploadData.url.startsWith('http://') && !uploadData.url.startsWith('https://')) {
    error.value = '图片URL必须以http://或https://开头'
    return
  }
  
  error.value = ''
  uploading.value = true
  
  try {
    const res = await fetch('/image/upload/url', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(uploadData)
    })
    
    const data = await res.json()
    if (data.code === 0) {
      emit('upload-success', data.data)
      emit('close')
    } else {
      error.value = data.message || '上传失败'
    }
  } catch (e) {
    error.value = '网络错误'
    console.error(e)
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.upload-modal-bg {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.upload-modal {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  padding: 36px 32px 28px 32px;
  width: 480px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
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

.upload-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.tab-btn {
  flex: 1;
  background: none;
  border: none;
  padding: 10px;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  transition: all 0.2s;
}

.tab-btn.active {
  color: #0099e5;
  border-bottom: 2px solid #0099e5;
}

.drop-area {
  border: 2px dashed #ccc;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.2s;
}

.drop-area:hover {
  border-color: #0099e5;
  background: #f8f9fa;
}

.upload-icon {
  font-size: 3rem;
  margin-bottom: 10px;
  color: #aaa;
}

.preview {
  text-align: center;
}

.preview img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
}

.form-group textarea {
  height: 80px;
  resize: vertical;
}

.upload-submit-btn {
  width: 100%;
  background: #0099e5;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 12px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
  margin-top: 10px;
}

.upload-submit-btn:hover {
  background: #007acc;
}

.upload-submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error {
  color: #e74c3c;
  margin-top: 15px;
  text-align: center;
}
</style>