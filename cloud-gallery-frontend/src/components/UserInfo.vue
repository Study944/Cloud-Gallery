<template>
  <div class="user-info" v-if="user">
    <h2>用户信息</h2>
    <p><strong>ID：</strong>{{ user.id }}</p>
    <p><strong>账号：</strong>{{ user.account }}</p>
    <p><strong>昵称：</strong>{{ user.username }}</p>
    <p><strong>角色：</strong>{{ user.role }}</p>
    <button @click="logout">退出登录</button>
  </div>
  <div v-else>
    <p>未登录</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const user = ref(null)

const fetchUser = async () => {
  try {
    const res = await fetch('/user/getLoginUser')
    const data = await res.json()
    if (data.code === 0) {
      user.value = data.data
    } else {
      user.value = null
    }
  } catch (e) {
    user.value = null
  }
}

const logout = async () => {
  await fetch('/user/logout')
  localStorage.removeItem('user')
  window.location.reload()
}

onMounted(() => {
  fetchUser()
})
</script>

<style scoped>
.user-info {
  max-width: 350px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
}
.user-info p {
  margin: 8px 0;
}
.user-info button {
  margin-top: 16px;
}
</style> 