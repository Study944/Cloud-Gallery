<template>
  <div class="private-space-container">
    <header class="space-header">
      <h1>我的私人空间</h1>
      <button class="create-space-btn" @click="showCreateSpaceModal = true">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        创建新空间
      </button>
    </header>

    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
      <span>加载空间中...</span>
    </div>

    <div v-else-if="spaces.length === 0" class="no-spaces">
      <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
        <circle cx="8.5" cy="8.5" r="1.5"></circle>
        <polyline points="21 15 16 10 5 21"></polyline>
      </svg>
      <p>您还没有创建私人空间</p>
      <button class="create-first-space" @click="showCreateSpaceModal = true">创建第一个空间</button>
    </div>

    <div v-else class="spaces-grid">
      <div v-for="space in spaces" :key="space.id" class="space-card" @click="navigateToSpace(space.id)">
        <div class="space-icon">🗄️</div>
        <h3 class="space-name">{{ space.name }}</h3>
        <p class="space-desc">{{ space.description || '无描述' }}</p>
        <div class="space-meta">
          <span>{{ space.imageCount }} 张图片</span>
          <span>{{ formatDate(space.createTime) }}</span>
        </div>
        <div class="space-actions">
          <button @click.stop="editSpace(space)">编辑</button>
          <button @click.stop="deleteSpace(space.id)" class="danger-btn">删除</button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑空间模态框 -->
    <Modal v-if="showCreateSpaceModal" @close="showCreateSpaceModal = false">
      <template #header>
        <h2>{{ editingSpace ? '编辑空间' : '创建新空间' }}</h2>
      </template>
      <div class="modal-content">
        <form @submit.prevent="handleSpaceSubmit">
          <div class="form-group">
            <label for="spaceName">空间名称</label>
            <input
              id="spaceName"
              v-model="spaceForm.name"
              type="text"
              maxlength="20"
              required
            />
          </div>
          <div class="form-group">
            <label for="spaceDesc">空间描述</label>
            <textarea
              id="spaceDesc"
              v-model="spaceForm.description"
              maxlength="100"
              rows="3"
            ></textarea>
          </div>
          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="showCreateSpaceModal = false">取消</button>
            <button type="submit" class="submit-btn">{{ editingSpace ? '更新' : '创建' }}</button>
          </div>
        </form>
      </div>
    </Modal>

    <!-- 确认删除模态框 -->
    <ConfirmModal
      v-if="showDeleteConfirm"
      @close="showDeleteConfirm = false"
      @confirm="confirmDelete"
      :message="'确定要删除这个空间吗？此操作不可恢复。'"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import Modal from '../components/Modal.vue';
import ConfirmModal from '../components/ConfirmModal.vue';
import axios from 'axios';
import { format } from 'date-fns';

const router = useRouter();
const spaces = ref([]);
const loading = ref(true);
const showCreateSpaceModal = ref(false);
const showDeleteConfirm = ref(false);
const editingSpace = ref(null);
const spaceForm = reactive({
  name: '',
  description: ''
});
const spaceToDelete = ref(null);

// 获取用户私人空间列表
const fetchSpaces = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/api/space/list', {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    });
    spaces.value = response.data.data;
  } catch (error) {
    console.error('获取空间列表失败:', error);
    alert('获取空间列表失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 创建或更新空间
const handleSpaceSubmit = async () => {
  try {
    const url = editingSpace.value ? `/api/space/update` : `/api/space/add`;
    const method = editingSpace.value ? 'post' : 'post';
    const data = editingSpace.value
      ? { ...spaceForm, id: editingSpace.value.id }
      : spaceForm;

    await axios[method](url, data, {
      headers: {
        'Authorization': localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    });

    showCreateSpaceModal.value = false;
    fetchSpaces();
    resetForm();
    editingSpace.value = null;
  } catch (error) {
    console.error('保存空间失败:', error);
    alert('保存空间失败，请重试');
  }
};

// 删除空间
const deleteSpace = (spaceId) => {
  spaceToDelete.value = spaceId;
  showDeleteConfirm.value = true;
};

const confirmDelete = async () => {
  try {
    await axios.get(`/api/space/delete?spaceId=${spaceToDelete.value}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    });
    fetchSpaces();
    showDeleteConfirm.value = false;
  } catch (error) {
    console.error('删除空间失败:', error);
    alert('删除空间失败，请重试');
  }
};

// 编辑空间
const editSpace = (space) => {
  editingSpace.value = space;
  spaceForm.name = space.name;
  spaceForm.description = space.description;
  showCreateSpaceModal.value = true;
};

// 导航到空间详情
const navigateToSpace = (spaceId) => {
  router.push({ name: 'SpaceDetail', params: { id: spaceId } });
};

// 重置表单
const resetForm = () => {
  spaceForm.name = '';
  spaceForm.description = '';
};

// 格式化日期
const formatDate = (dateString) => {
  return format(new Date(dateString), 'yyyy-MM-dd');
};

onMounted(() => {
  fetchSpaces();
});
</script>

<style scoped>
.private-space-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.space-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.create-space-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.create-space-btn:hover {
  background-color: #359469;
}

.spaces-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.space-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.space-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.space-icon {
  font-size: 24px;
  margin-bottom: 10px;
}

.space-name {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
}

.space-desc {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.space-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #f0f0f0;
}

.space-actions {
  display: none;
  position: absolute;
  top: 10px;
  right: 10px;
  gap: 5px;
}

.space-card:hover .space-actions {
  display: flex;
}

.space-actions button {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 3px;
  border: none;
  cursor: pointer;
}

.danger-btn {
  background-color: #ff4d4f;
  color: white;
}

.no-spaces {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.no-spaces svg {
  margin-bottom: 20px;
  color: #ccc;
}

.create-first-space {
  margin-top: 20px;
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.cancel-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

.submit-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>