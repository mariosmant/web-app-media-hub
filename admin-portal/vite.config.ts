// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import dotenv from 'dotenv';
import path from 'path';

export default defineConfig(({ command, mode }) => {
  // Choose .env file.
  const appEnv = process.env.APP_ENV || '';
  if(!appEnv) {
    const appEnvIsRequiredErrorMsg = 'APP_ENV is required. Example: APP_ENV=dev vite --mode dev (use cross-env on Windows).';
    console.error(appEnvIsRequiredErrorMsg)
    throw new Error(appEnvIsRequiredErrorMsg);
  }
  console.log(`Using APP_ENV="${appEnv}" / mode="${mode}"`);

  // Load .env file
  let pathEnvFile = path.resolve(__dirname, `env/.env.${appEnv}`);
  dotenv.config({
    path: pathEnvFile
  });
  console.log(`Loaded ${pathEnvFile}`);

  return {
    plugins: [react()],
      ...(command === 'serve' && mode == 'dev' && { 
        server: {
          port: 5173,
          strictPort: true
        }
      }),
  };
});
