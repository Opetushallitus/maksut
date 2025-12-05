import { OpenNextConfig } from '@opennextjs/aws/types/open-next';

const config = {
  default: {},
  buildCommand: 'pnpm run build',
  packageJsonPath: './package.json',
} satisfies OpenNextConfig;

export default config;
