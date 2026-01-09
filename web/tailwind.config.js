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
          bg: 'var(--sj-bg)',
          surface: 'var(--sj-surface)',
          lighter: 'var(--sj-lighter)',
          accent: '#533483',
          purple: '#7c3aed',
          blue: '#3b82f6',
        },
        light: {
          text: 'var(--sj-text)',
          muted: 'var(--sj-muted)',
        }
      }
    },
  },
  plugins: [],
}
