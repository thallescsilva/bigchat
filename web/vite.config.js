var _a;
/// <reference types="vitest" />
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
var apiTarget = (_a = process.env.VITE_API_PROXY) !== null && _a !== void 0 ? _a : 'http://localhost:8080';
var proxied = ['/auth', '/conversations', '/messages', '/clients', '/v3', '/swagger-ui'];
export default defineConfig({
    plugins: [react()],
    server: {
        port: 5173,
        proxy: Object.fromEntries(proxied.map(function (path) { return [path, { target: apiTarget, changeOrigin: true }]; })),
    },
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: './src/test/setup.ts',
        css: false,
    },
});
