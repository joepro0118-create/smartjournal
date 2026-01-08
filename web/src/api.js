// API service for connecting to Java backend

const API_BASE_URL = 'http://localhost:8080/api';

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
      const error = await response.json();
      throw new Error(error.error || 'Login failed');
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
      const error = await response.json();
      throw new Error(error.error || 'Registration failed');
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
      const error = await response.json();
      throw new Error(error.error || 'Failed to save entry');
    }

    return response.json();
  },
};

