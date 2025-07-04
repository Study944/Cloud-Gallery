<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-content">
      <h3>{{ title }}</h3>
      <p>{{ message }}</p>
      <div class="modal-buttons">
        <button @click="onCancel">{{ cancelText }}</button>
        <button @click="onConfirm">{{ confirmText }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '确认' },
  message: { type: String, default: '确定要执行此操作吗?' },
  cancelText: { type: String, default: '取消' },
  confirmText: { type: String, default: '确认' }
});

const emit = defineEmits(['cancel', 'confirm']);

const onCancel = () => emit('cancel');
const onConfirm = () => emit('confirm');
</script>

<style scoped>
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; }
.modal-content { background: white; padding: 20px; border-radius: 8px; }
.modal-buttons { margin-top: 20px; display: flex; gap: 10px; justify-content: flex-end; }
button { padding: 8px 16px; border-radius: 4px; cursor: pointer; }
button:first-child { border: 1px solid #ddd; background: white; }
button:last-child { border: none; background: #ff4d4f; color: white; }
</style>