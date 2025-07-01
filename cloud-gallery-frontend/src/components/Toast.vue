<template>
  <div class="toast" :class="{ 'toast-success': type === 'success', 'toast-error': type === 'error' }" v-if="show">
    {{ message }}
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'success'
  },
  duration: {
    type: Number,
    default: 3000
  }
})

const show = ref(true)

onMounted(() => {
  setTimeout(() => {
    show.value = false
  }, props.duration)
})
</script>

<style scoped>
.toast {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 4px;
  color: white;
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

.toast-success {
  background: #52c41a;
}

.toast-error {
  background: #ff4d4f;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>