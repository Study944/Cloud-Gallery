<template>
  <div class="login-modal-bg" @click.self="$emit('close')">
    <div class="login-modal">
      <button class="close-btn" @click="$emit('close')">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
      
      <div class="login-header">
        <div class="login-logo">🖼️</div>
        <h2>登录到 Cloud Gallery</h2>
        <p class="login-subtitle">探索和分享高质量图片素材</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="input-group">
          <label for="account">账号</label>
          <div class="input-wrapper">
            <span class="input-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </span>
            <input 
              id="account"
              v-model="account" 
              type="text" 
              placeholder="请输入账号" 
              required 
              autocomplete="username"
            />
          </div>
        </div>
        
        <div class="input-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
            </span>
            <input 
              id="password"
              v-model="password" 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="请输入密码" 
              required 
              autocomplete="current-password"
            />
            <button 
              type="button" 
              class="toggle-password" 
              @click="showPassword = !showPassword"
            >
              <svg v-if="!showPassword" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
                <line x1="1" y1="1" x2="23" y2="23"></line>
              </svg>
            </button>
          </div>
        </div>
        
        <div class="login-options">
          <label class="remember-me">
            <input type="checkbox" v-model="rememberMe" />
            <span>记住我</span>
          </label>
          <a href="#" class="forgot-password">忘记密码?</a>
        </div>
        
        <button class="login-btn" type="submit" :disabled="loading">
          <span v-if="loading" class="btn-spinner"></span>
          <span v-else>登录</span>
        </button>
      </form>
      
      <div v-if="error" class="error-message">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        {{ error }}
      </div>
      
      <div class="login-footer">
        <p>还没有账号? <a href="#" @click.prevent="$emit('switch-to-register')">立即注册</a></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 定义要发出的事件
const emit = defineEmits(['close', 'login-success', 'switch-to-register'])

const account = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)
const showPassword = ref(false)
const rememberMe = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true
  
  try {
    const res = await fetch('/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account: account.value, password: password.value }),
      credentials: 'include'
    })
    
    const data = await res.json()
    if (data.code === 0 || data.code === 20000) {
      localStorage.setItem('user', JSON.stringify(data.data))
      emit('login-success', data.data)
    } else {
      error.value = data.message || '登录失败，请检查账号和密码'
    }
  } catch (e) {
    error.value = '网络错误，请稍后再试'
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-modal-bg {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.login-modal {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: 32px;
  width: 400px;
  max-width: 90vw;
  position: relative;
  animation: modal-in 0.3s ease-out;
}

@keyframes modal-in {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.close-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.close-btn:hover {
  color: var(--text-primary);
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.login-logo {
  font-size: 3rem;
  margin-bottom: 16px;
}

.login-header h2 {
  font-size: 1.5rem;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.login-subtitle {
  color: var(--text-tertiary);
  margin: 0;
}

.login-form {
  margin-bottom: 16px;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: var(--text-primary);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-light);
  transition: all 0.2s;
}

.input-wrapper:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 153, 229, 0.1);
}

.input-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 12px;
  color: var(--text-tertiary);
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  padding: 12px 12px 12px 0;
  font-size: 1rem;
  color: var(--text-primary);
}

.toggle-password {
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: 0 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.toggle-password:hover {
  color: var(--text-primary);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 0.9rem;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: var(--text-secondary);
}

.forgot-password {
  color: var(--primary-color);
  text-decoration: none;
}

.forgot-password:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  padding: 12px 0;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-btn:hover:not(:disabled) {
  background: var(--primary-dark);
}

.login-btn:disabled {
  background: var(--text-tertiary);
  cursor: not-allowed;
}

.btn-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ffebee;
  color: #d32f2f;
  padding: 12px;
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  font-size: 0.9rem;
}

.login-footer {
  text-align: center;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.login-footer a {
  color: var(--primary-color);
  font-weight: 500;
  text-decoration: none;
}

.login-footer a:hover {
  text-decoration: underline;
}
</style>