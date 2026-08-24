import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { resolve } from 'path';

export default defineConfig({
  plugins: [react()],
  base: '/maksut/',
  build: {
    outDir: '../../resources/public/maksut',
    emptyOutDir: true,
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, '.'),
    },
  },
  server: {
    port: 5174,
    proxy: {
      '/maksut/api': {
        target: 'https://localhost:9000',
        secure: false,
        changeOrigin: true,
      },
    },
  },
});
