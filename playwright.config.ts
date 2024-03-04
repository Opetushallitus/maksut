import { defineConfig } from '@playwright/test';
export default defineConfig({
    /* Fail the build on CI if you accidentally left test.only in the source code. */
    forbidOnly: Boolean(process.env.CI),
    /* Retry on CI only */
    retries: process.env.CI ? 1 : 0,
    /* Reporter to use. See https://playwright.dev/docs/test-reporters */
    reporter: 'list',
    use: {
        // All requests we send go to this API endpoint.
        baseURL: process.env.WITH_PAYTRAIL == "TRUE" ? 'https://maksut-local.test:9000' : 'http://localhost:19033',
        /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
        trace: 'on-first-retry',
    }
});