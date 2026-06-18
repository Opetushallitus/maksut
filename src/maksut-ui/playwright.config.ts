import { defineConfig } from '@playwright/test';
export default defineConfig({
  testDir: './playwright/tests',
  forbidOnly: Boolean(process.env.CI),
  retries: process.env.CI ? 1 : 0,
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:19033/maksut',
    trace: 'on-first-retry',
  },
});
