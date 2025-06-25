<script setup>
import { ref, computed } from 'vue'
import UserLogin from './components/UserLogin.vue'
import UserRegister from './components/UserRegister.vue'
import UserInfo from './components/UserInfo.vue'
</script>

<template>
  <div id="app">
    <h1>图片素材网站</h1>
    <div v-if="!isLogin">
      <button @click="showLogin = true">登录</button>
      <button @click="showLogin = false">注册</button>
      <UserLogin v-if="showLogin" />
      <UserRegister v-else />
    </div>
    <div v-else>
      <UserInfo />
    </div>
  </div>
</template>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 30px;
}
button {
  margin: 0 8px 16px 8px;
  padding: 6px 18px;
  border-radius: 4px;
  border: 1px solid #aaa;
  background: #f8f8f8;
  cursor: pointer;
}
button:hover {
  background: #e6e6e6;
}
</style>
