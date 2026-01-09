const THEME_KEY = 'smart_journal_theme';

export const getInitialTheme = () => {
  const saved = localStorage.getItem(THEME_KEY);
  if (saved === 'light' || saved === 'dark') return saved;

  // Default: dark (matches current app)
  return 'dark';
};

export const applyTheme = (theme) => {
  const root = document.documentElement;
  if (theme === 'light') {
    root.classList.add('theme-light');
  } else {
    root.classList.remove('theme-light');
  }
};

export const setStoredTheme = (theme) => {
  localStorage.setItem(THEME_KEY, theme);
};

export const toggleThemeValue = (theme) => (theme === 'light' ? 'dark' : 'light');

