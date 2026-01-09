import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Set BACKEND_PORT to match your Spring Boot port (default 8080)
const backendPort = process.env.BACKEND_PORT || '8080';

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    port: 5173,
    proxy: {
      '/api': {
        target: `http://localhost:${backendPort}`,
        changeOrigin: true,
      },
    },
  },
})
