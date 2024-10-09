import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// jsconfig 에서 절대경로 설정이 아직 안됨
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
