import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/user': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      '/image': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
    },
  },
})
