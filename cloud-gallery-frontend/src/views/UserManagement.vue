<template>
  <div class="admin-page">
    <header class="admin-header">
      <h1>用户管理</h1>
      <button class="back-btn" @click="goBack">返回首页</button>
    </header>
    
    <div class="user-filters">
      <select v-model="role" @change="fetchUsers">
        <option value="">所有角色</option>
        <option value="admin">管理员</option>
        <option value="user">普通用户</option>
      </select>
      <div class="search-box">
        <input v-model="search" placeholder="搜索用户名或账号..." @keyup.enter="fetchUsers">
        <button @click="fetchUsers">搜索</button>
      </div>
    </div>
    
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
      <span>加载中...</span>
    </div>
    
    <div v-else-if="users.length === 0" class="no-results">
      <p>没有找到用户</p>
    </div>
    
    <div class="user-table" v-else>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>账号</th>
            <th>角色</th>
            <th>注册时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username || '未设置' }}</td>
            <td>{{ user.account }}</td>
            <td>{{ user.role || 'user' }}</td>
            <td>{{ formatDate(user.createTime) }}</td>
            <td>
              <span class="status-badge" :class="user.status === 'active' ? 'active' : 'inactive'">{{ user.status === 'active' ? '正常' : '禁用' }}</span>
            </td>
            <td class="action-buttons">
              <button class="edit-btn" @click="handleEditRole(user)">修改角色</button>
              <button 
                class="status-btn" 
                :class="user.status === 'active' ? 'disable-btn' : 'enable-btn'"
                @click="handleToggleStatus(user)"
              >
                {{ user.status === 'active' ? '禁用' : '启用' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="pagination" v-if="total > 0">
      <button :disabled="currentPage === 1" @click="currentPage--; fetchUsers()">上一页</button>
      <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
      <button :disabled="currentPage >= totalPages" @click="currentPage++; fetchUsers()">下一页</button>
    </div>

    <!-- 修改角色弹窗 -->
    <div class="modal-bg" v-if="showEditRoleModal">
      <div class="modal">
        <h3>修改角色 - {{ editUser.username }}</h3>
        <div class="modal-content">
          <label>选择角色:</label>
          <select v-model="newRole">
            <option value="user">普通用户</option>
            <option value="admin">管理员</option>
          </select>
        </div>
        <div class="modal-actions">
          <button class="cancel-btn" @click="showEditRoleModal = false">取消</button>
          <button class="confirm-btn" @click="confirmEditRole">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Toast from '../components/Toast.vue'

const router = useRouter()
const users = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const role = ref('')
const search = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')
const showEditRoleModal = ref(false)
const editUser = ref(null)
const newRole = ref('user')

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

const fetchUsers = async () => {
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
    if (role.value) params.append('role', role.value)
    if (search.value) params.append('search', search.value)

    const res = await fetch(`/admin/users?${params.toString()}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (!res.ok) throw new Error('获取用户列表失败')

    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      users.value = data.data.records || []
      total.value = data.data.total || 0
    } else {
      throw new Error(data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('Error fetching users:', error)
    showMessage(error.message || '获取用户失败', 'error')
    users.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleEditRole = (user) => {
  editUser.value = { ...user }
  newRole.value = user.role || 'user'
  showEditRoleModal.value = true
}

const confirmEditRole = async () => {
  if (!editUser.value) return

  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`/admin/users/${editUser.value.id}/role`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ role: newRole.value })
    })

    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      // 更新本地用户列表
      const index = users.value.findIndex(u => u.id === editUser.value.id)
      if (index !== -1) {
        users.value[index].role = newRole.value
      }
      showMessage('角色修改成功')
      showEditRoleModal.value = false
    } else {
      throw new Error(data.message || '角色修改失败')
    }
  } catch (error) {
    console.error('Error updating role:', error)
    showMessage(error.message || '角色修改失败', 'error')
  }
}

const handleToggleStatus = async (user) => {
  try {
    const token = localStorage.getItem('token')
    const newStatus = user.status === 'active' ? 'inactive' : 'active'
    const res = await fetch(`/admin/users/${user.id}/status`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status: newStatus })
    })

    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      user.status = newStatus
      showMessage(`${newStatus === 'active' ? '启用' : '禁用'}用户成功`)
    } else {
      throw new Error(data.message || '状态修改失败')
    }
  } catch (error) {
    console.error('Error toggling status:', error)
    showMessage(error.message || '状态修改失败', 'error')
  }
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  fetchUsers()
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

.user-filters {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.user-filters select {
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

.user-table {
  overflow-x: auto;
  margin-top: 20px;
}

.user-table table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th, .user-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.user-table th {
  background-color: #f9f9f9;
  font-weight: bold;
}

.status-badge {
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
}

.status-badge.active {
  background-color: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background-color: #f8d7da;
  color: #721c24;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn {
  padding: 5px 10px;
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.status-btn {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.disable-btn {
  background-color: #e74c3c;
  color: white;
}

.enable-btn {
  background-color: #2ecc71;
  color: white;
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

/* 模态框样式 */
.modal-bg {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  padding: 20px;
}

.modal h3 {
  margin-top: 0;
  margin-bottom: 20px;
}

.modal-content {
  margin-bottom: 20px;
}

.modal-content label {
  display: block;
  margin-bottom: 8px;
}

.modal-content select {
  width: 100%;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn {
  padding: 8px 16px;
  background-color: #666;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.confirm-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>