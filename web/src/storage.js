// LocalStorage utilities for journal entries

const STORAGE_KEY = 'smart_journal_entries';

export const getEntries = () => {
  const stored = localStorage.getItem(STORAGE_KEY);
  return stored ? JSON.parse(stored) : [];
};

export const saveEntry = (entry) => {
  const entries = getEntries();
  const existingIndex = entries.findIndex(e => e.id === entry.id);

  if (existingIndex >= 0) {
    entries[existingIndex] = entry;
  } else {
    entries.push(entry);
  }

  localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
  return entries;
};

export const deleteEntry = (id) => {
  const entries = getEntries();
  const filtered = entries.filter(e => e.id !== id);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(filtered));
  return filtered;
};

export const createNewEntry = () => {
  return {
    id: Date.now().toString(),
    title: 'Untitled Entry',
    content: '',
    date: new Date().toISOString(),
    preview: ''
  };
};

export const formatDate = (dateString) => {
  const date = new Date(dateString);
  const options = {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  };
  return date.toLocaleDateString('en-US', options);
};

export const formatShortDate = (dateString) => {
  const date = new Date(dateString);
  const options = {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  };
  return date.toLocaleDateString('en-US', options);
};

export const generatePreview = (content, maxLength = 100) => {
  if (!content) return 'No content yet...';
  const stripped = content.replace(/\n/g, ' ').trim();
  return stripped.length > maxLength
    ? stripped.substring(0, maxLength) + '...'
    : stripped;
};

