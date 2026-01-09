// API service for connecting to Java backend

// Use same-origin + Vite proxy (see vite.config.js)
const API_BASE_URL = '/api';

export const authAPI = {
  async login(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      let message = 'Login failed';
      try {
        const error = await response.json();
        message = error.error || message;
      } catch (_) {
        // ignore JSON parse errors
      }
      throw new Error(message);
    }

    return response.json();
  },

  async register(email, displayName, password) {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, displayName, password }),
    });

    if (!response.ok) {
      let message = 'Registration failed';
      try {
        const error = await response.json();
        message = error.error || message;
      } catch (_) {
        // ignore
      }
      throw new Error(message);
    }

    return response.json();
  },
};

export const journalAPI = {
  async getEntries(email) {
    const response = await fetch(`${API_BASE_URL}/journal/entries?email=${encodeURIComponent(email)}`);

    if (!response.ok) {
      throw new Error('Failed to fetch entries');
    }

    return response.json();
  },

  async saveEntry(email, date, content) {
    const response = await fetch(`${API_BASE_URL}/journal/save`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, date, content }),
    });

    if (!response.ok) {
      let message = 'Failed to save entry';
      try {
        const error = await response.json();
        message = error.error || message;
      } catch (_) {
        // ignore
      }
      throw new Error(message);
    }

    return response.json();
  },
};
