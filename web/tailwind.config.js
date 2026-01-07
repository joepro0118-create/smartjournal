/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        dark: {
          bg: '#1a1a2e',
          surface: '#16213e',
          lighter: '#0f3460',
          accent: '#533483',
          purple: '#7c3aed',
          blue: '#3b82f6',
        },
        light: {
          text: '#e9e9f0',
          muted: '#b4b4c8',
        }
      }
    },
  },
  plugins: [],
}

