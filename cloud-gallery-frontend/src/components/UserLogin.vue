<template>
  <div class="login-modal-bg">
    <div class="login-modal">
      <button class="close-btn" @click="$emit('close')">×</button>
      <h2>登录到 Cloud Gallery</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <span class="icon">👤</span>
          <input v-model="account" type="text" placeholder="账号" required />
        </div>
        <div class="input-group">
          <span class="icon">🔒</span>
          <input v-model="password" type="password" placeholder="密码" required />
        </div>
        <button class="login-btn" type="submit">登录</button>
      </form>
      <div v-if="error" class="error">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const account = ref('')
const password = ref('')
const error = ref('')

const handleLogin = async () => {
  error.value = ''
  try {
    const res = await fetch('/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account: account.value, password: password.value })
    })
    const data = await res.json()
    if (data.code === 0) {
      localStorage.setItem('user', JSON.stringify(data.data))
      window.location.reload()
    } else {
      error.value = data.message || '登录失败'
    }
  } catch (e) {
    error.value = '网络错误'
  }
}
</script>

<style scoped>
.login-modal-bg {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.login-modal {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  padding: 36px 32px 28px 32px;
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
.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fafbfc;
  padding: 0 10px;
}
.input-group .icon {
  font-size: 1.2rem;
  margin-right: 6px;
  color: #aaa;
}
.input-group input {
  border: none;
  background: transparent;
  outline: none;
  flex: 1;
  padding: 10px 0;
  font-size: 1rem;
}
.login-btn {
  width: 100%;
  background: #0099e5;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 0;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 8px;
  transition: background 0.2s;
}
.login-btn:hover {
  background: #007acc;
}
.error {
  color: #e74c3c;
  margin-top: 12px;
}
</style> 