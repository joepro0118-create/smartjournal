// Storage utilities for journal entries
import { journalAPI } from './api';

const STORAGE_KEY = 'smart_journal_entries';

// Get current user from session
const getCurrentUser = () => {
  const user = localStorage.getItem('smart_journal_current_user');
  return user ? JSON.parse(user) : null;
};

export const getEntries = async () => {
  const user = getCurrentUser();
  if (!user) return [];

  try {
    const backendEntries = await journalAPI.getEntries(user.email);

    // Convert backend format to frontend format
    return backendEntries.map(entry => ({
      id: entry.id,
      title: entry.date || 'Untitled Entry',
      content: entry.content || '',
      date: entry.date ? new Date(entry.date).toISOString() : new Date().toISOString(),
      preview: generatePreview(entry.content),
      weather: entry.weather || '',
      mood: entry.mood || ''
    }));
  } catch (error) {
    console.error('Failed to load entries from backend:', error);
    // Fallback to localStorage
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  }
};

export const saveEntry = async (entry) => {
  const user = getCurrentUser();
  if (!user) {
    throw new Error('No user logged in');
  }

  try {
    // Extract date from entry (format: YYYY-MM-DD)
    const date = new Date(entry.date).toISOString().split('T')[0];

    // Save to backend
    const response = await journalAPI.saveEntry(user.email, date, entry.content);

    // Update the entry with weather and mood from backend
    entry.weather = response.weather;
    entry.mood = response.mood;

    // Also save to localStorage as backup
    const entries = localStorage.getItem(STORAGE_KEY);
    const parsed = entries ? JSON.parse(entries) : [];
    const existingIndex = parsed.findIndex(e => e.id === entry.id);

    if (existingIndex >= 0) {
      parsed[existingIndex] = entry;
    } else {
      parsed.push(entry);
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(parsed));

    // Reload entries from backend
    return await getEntries();
  } catch (error) {
    console.error('Failed to save to backend:', error);
    // Fallback to localStorage only
    const entries = localStorage.getItem(STORAGE_KEY);
    const parsed = entries ? JSON.parse(entries) : [];
    const existingIndex = parsed.findIndex(e => e.id === entry.id);

    if (existingIndex >= 0) {
      parsed[existingIndex] = entry;
    } else {
      parsed.push(entry);
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(parsed));
    return parsed;
  }
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

