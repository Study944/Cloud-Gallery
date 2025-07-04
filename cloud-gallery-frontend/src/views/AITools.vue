<template>
  <div class="ai-tools-container">
    <header class="ai-header">
      <h1>AI 工具</h1>
      <p class="ai-subtitle">使用人工智能生成图片和描述</p>
    </header>

    <div class="tools-tabs">
      <button class="tab-btn" :class="{ active: activeTab === 'generateImage' }" @click="activeTab = 'generateImage'">
        文本生成图片
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'generateDescription' }" @click="activeTab = 'generateDescription'">
        图片生成描述
      </button>
    </div>

    <div class="tab-content">
      <!-- 文本生成图片 -->
      <div v-if="activeTab === 'generateImage'" class="tool-panel">
        <div class="prompt-input-section">
          <label for="imagePrompt">描述文本</label>
          <textarea
            id="imagePrompt"
            v-model="imagePrompt"
            placeholder="请输入图片描述，例如：'一只可爱的柯基犬在草地上奔跑，阳光明媚，卡通风格'"
            rows="4"
            maxlength="500"
          ></textarea>
          <div class="prompt-hint">提示：描述越详细，生成效果越好</div>
          <button
            class="generate-btn"
            @click="generateImage"
            :disabled="!imagePrompt.trim() || generatingImage"
          >
            <span v-if="!generatingImage">生成图片</span>
            <span v-if="generatingImage">生成中...</span>
          </button>
        </div>

        <div v-if="generatingImage" class="loading-section">
          <div class="spinner"></div>
          <p>AI正在努力生成图片，请稍候...</p>
        </div>

        <div v-if="generatedImageUrl" class="result-section">
          <h3>生成结果</h3>
          <div class="image-container">
            <img :src="generatedImageUrl" alt="AI生成图片" class="generated-image" />
          </div>
          <div class="result-actions">
            <button class="action-btn" @click="downloadImage">下载图片</button>
            <button class="action-btn" @click="saveToSpace">保存到私人空间</button>
          </div>
        </div>

        <div v-if="imageError" class="error-section">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
          <p>{{ imageError }}</p>
        </div>
      </div>

      <!-- 图片生成描述 -->
      <div v-if="activeTab === 'generateDescription'" class="tool-panel">
        <div class="image-upload-section">
          <label>选择图片</label>
          <div class="upload-container">
            <input
              type="file"
              id="imageUpload"
              accept="image/*"
              @change="handleImageUpload"
              class="file-input"
            />
            <label for="imageUpload" class="upload-label">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="17 8 12 3 7 8"></polyline>
                <line x1="12" y1="3" x2="12" y2="15"></line>
              </svg>
              <span v-if="!selectedImageUrl">点击上传图片</span>
              <span v-if="selectedImageUrl">更换图片</span>
            </label>
          </div>

          <div v-if="selectedImageUrl" class="preview-container">
            <img :src="selectedImageUrl" alt="预览图片" class="preview-image" />
            <button class="remove-image" @click="removeImage">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>

          <button
            class="generate-btn"
            @click="generateDescription"
            :disabled="!selectedImageUrl || generatingDescription"
          >
            <span v-if="!generatingDescription">生成描述</span>
            <span v-if="generatingDescription">生成中...</span>
          </button>
        </div>

        <div v-if="generatingDescription" class="loading-section">
          <div class="spinner"></div>
          <p>AI正在分析图片内容，请稍候...</p>
        </div>

        <div v-if="imageDescription" class="description-result">
          <h3>图片描述</h3>
          <div class="description-content">
            <p>{{ imageDescription }}</p>
          </div>
          <div class="result-actions">
            <button class="action-btn" @click="copyDescription">复制描述</button>
          </div>
        </div>

        <div v-if="descriptionError" class="error-section">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
          <p>{{ descriptionError }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
// 标签页状态
const activeTab = ref('generateImage');

// 文本生成图片相关状态
const imagePrompt = ref('');
const generatedImageUrl = ref('');
const generatingImage = ref(false);
const imageError = ref('');

// 图片生成描述相关状态
const selectedImageUrl = ref('');
const imageFile = ref(null);
const imageDescription = ref('');
const generatingDescription = ref(false);
const descriptionError = ref('');

// 生成图片
const generateImage = async () => {
  if (!imagePrompt.value.trim()) return;

  generatingImage.value = true;
  imageError.value = '';
  generatedImageUrl.value = '';

  try {
    const response = await axios.get('/api/image/generateImage', {
      params: { prompt: imagePrompt.value },
      headers: {
        'Authorization': localStorage.getItem('token')
      },
      responseType: 'json'
    });

    if (response.data.code === 0 && response.data.data && response.data.data.url) {
      generatedImageUrl.value = response.data.data.url;
    } else {
      imageError.value = '生成图片失败: ' + (response.data.message || '未知错误');
    }
  } catch (error) {
    console.error('生成图片API调用失败:', error);
    imageError.value = '生成图片失败，请检查网络连接或稍后重试';
  } finally {
    generatingImage.value = false;
  }
};

// 处理图片上传
const handleImageUpload = (e) => {
  const file = e.target.files[0];
  if (file) {
    imageFile.value = file;
    selectedImageUrl.value = URL.createObjectURL(file);
    descriptionError.value = '';
    imageDescription.value = '';
  }
};

// 移除已选择图片
const removeImage = () => {
  selectedImageUrl.value = '';
  imageFile.value = null;
  document.getElementById('imageUpload').value = '';
};

// 生成图片描述
const generateDescription = async () => {
  if (!imageFile.value) return;

  generatingDescription.value = true;
  descriptionError.value = '';
  imageDescription.value = '';

  try {
    // 先上传图片获取imageId
    const formData = new FormData();
    formData.append('file', imageFile.value);
    formData.append('name', imageFile.value.name);
    formData.append('spaceId', 0); // 使用临时空间ID

    const uploadResponse = await axios.post('/api/image/upload/file', formData, {
      headers: {
        'Authorization': localStorage.getItem('token'),
        'Content-Type': 'multipart/form-data'
      }
    });

    if (uploadResponse.data.code !== 0 || !uploadResponse.data.data.id) {
      throw new Error('图片上传失败: ' + (uploadResponse.data.message || '未知错误'));
    }

    const imageId = uploadResponse.data.data.id;

    // 调用生成描述API
    const descResponse = await axios.get('/api/image/generateDescription', {
      params: { imageId },
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    });

    if (descResponse.data.code === 0 && descResponse.data.data && descResponse.data.data.description) {
      imageDescription.value = descResponse.data.data.description;
    } else {
      descriptionError.value = '生成描述失败: ' + (descResponse.data.message || '未知错误');
    }
  } catch (error) {
    console.error('生成描述API调用失败:', error);
    descriptionError.value = error.message || '生成描述失败，请检查网络连接或稍后重试';
  } finally {
    generatingDescription.value = false;
  }
};

// 下载图片
const downloadImage = () => {
  if (!generatedImageUrl.value) return;
  const link = document.createElement('a');
  link.href = generatedImageUrl.value;
  link.download = 'ai-generated-image.jpg';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

// 保存到私人空间
const saveToSpace = () => {
  if (!generatedImageUrl.value) return;
  // 这里可以实现选择空间并保存的逻辑
  alert('功能开发中：保存到私人空间');
};

// 复制描述文本
const copyDescription = () => {
  if (!imageDescription.value) return;
  navigator.clipboard.writeText(imageDescription.value).then(() => {
    alert('描述已复制到剪贴板');
  }).catch(err => {
    console.error('复制失败:', err);
    alert('复制失败，请手动复制');
  });
};
</script>

<style scoped>
.ai-tools-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.ai-header {
  text-align: center;
  margin-bottom: 30px;
}

.ai-subtitle {
  color: #666;
  margin-top: 8px;
  font-weight: normal;
}

.tools-tabs {
  display: flex;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  position: relative;
}

.tab-btn.active {
  color: #42b983;
  font-weight: 500;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #42b983;
}

.tool-panel {
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.prompt-input-section label,
.image-upload-section label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: #333;
}

textarea,
input[type="file"] {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
}

.prompt-hint {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
  margin-bottom: 15px;
}

.generate-btn {
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  margin-top: 15px;
}

.generate-btn:disabled {
  background-color: #b3e0c7;
  cursor: not-allowed;
}

.loading-section {
  text-align: center;
  padding: 40px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.result-section,
.description-result {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px dashed #eee;
}

.result-section h3,
.description-result h3 {
  margin-top: 0;
  color: #333;
}

.image-container {
  text-align: center;
  margin: 20px 0;
}

.generated-image,
.preview-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.result-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #42b983;
  color: #42b983;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.action-btn:hover {
  background-color: #f5fcf8;
}

.upload-container {
  border: 2px dashed #ddd;
  border-radius: 6px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-container:hover {
  border-color: #42b983;
}

.file-input {
  display: none;
}

.upload-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  color: #666;
}

.upload-label svg {
  margin-bottom: 10px;
  font-size: 24px;
}

.preview-container {
  position: relative;
  margin-top: 20px;
  text-align: center;
}

.remove-image {
  position: absolute;
  top: -10px;
  right: calc(50% - 200px);
  background-color: rgba(255,255,255,0.8);
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

.error-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #fff1f0;
  border: 1px solid #ffccc7;
  border-radius: 4px;
  color: #f5222d;
  display: flex;
  align-items: center;
  gap: 10px;
}

.description-content {
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin: 15px 0;
  line-height: 1.6;
  color: #333;
}
</style>