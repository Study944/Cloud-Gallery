<template>
  <div class="user-register">
    <h2>用户注册</h2>
    <form @submit.prevent="handleRegister">
      <div>
        <label>账号：</label>
        <input v-model="account" type="text" required />
      </div>
      <div>
        <label>密码：</label>
        <input v-model="password" type="password" required />
      </div>
      <button type="submit">注册</button>
    </form>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="success" class="success">注册成功，请登录！</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const account = ref('')
const password = ref('')
const error = ref('')
const success = ref(false)

const handleRegister = async () => {
  error.value = ''
  success.value = false
  try {
    const res = await fetch('/user/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account: account.value, password: password.value })
    })
    const data = await res.json()
    if (data.code === 0) {
      success.value = true
      account.value = ''
      password.value = ''
    } else {
      error.value = data.message || '注册失败'
    }
  } catch (e) {
    error.value = '网络错误'
  }
}
</script>

<style scoped>
.user-register {
  max-width: 300px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
}
.user-register label {
  display: inline-block;
  width: 60px;
}
.user-register input {
  width: 180px;
  margin-bottom: 12px;
}
.user-register .error {
  color: red;
  margin-top: 10px;
}
.user-register .success {
  color: green;
  margin-top: 10px;
}
</style> 