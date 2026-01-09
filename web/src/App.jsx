import React, { useState, useEffect } from 'react';
import Sidebar from './components/Sidebar';
import TopBar from './components/TopBar';
import Editor from './components/Editor';
import Login from './components/Login';
import WeeklySummary from './components/WeeklySummary';
import { getEntries, saveEntry, createNewEntry } from './storage';
import { applyTheme, getInitialTheme, setStoredTheme, toggleThemeValue } from './theme';

function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [entries, setEntries] = useState([]);
  const [selectedEntry, setSelectedEntry] = useState(null);
  const [currentEntry, setCurrentEntry] = useState(null);
  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);
  const [activeView, setActiveView] = useState('journal'); // 'journal' | 'weekly'
  const [theme, setTheme] = useState(getInitialTheme()); // 'dark' | 'light'

  // Apply theme on mount + when it changes
  useEffect(() => {
    applyTheme(theme);
    setStoredTheme(theme);
  }, [theme]);

  // Check for existing session on mount
  useEffect(() => {
    const savedUser = localStorage.getItem('smart_journal_current_user');
    if (savedUser) {
      setCurrentUser(JSON.parse(savedUser));
    }
  }, []);

  // Load entries on mount
  useEffect(() => {
    if (!currentUser) return;

    const loadEntries = async () => {
      try {
        const loadedEntries = await getEntries();
        // Sort by date, newest first
        const sorted = loadedEntries.sort((a, b) =>
          new Date(b.date) - new Date(a.date)
        );
        setEntries(sorted);
      } catch (error) {
        console.error('Failed to load entries:', error);
      }
    };

    loadEntries();
  }, [currentUser]);

  const handleLogin = (user) => {
    setCurrentUser(user);
    localStorage.setItem('smart_journal_current_user', JSON.stringify(user));
  };

  const handleLogout = () => {
    if (hasUnsavedChanges) {
      const confirm = window.confirm('You have unsaved changes. Do you want to discard them?');
      if (!confirm) return;
    }
    setCurrentUser(null);
    localStorage.removeItem('smart_journal_current_user');
    setEntries([]);
    setSelectedEntry(null);
    setCurrentEntry(null);
    setHasUnsavedChanges(false);
  };

  const handleNewEntry = () => {
    const newEntry = createNewEntry();
    setCurrentEntry(newEntry);
    setSelectedEntry(newEntry);
    setHasUnsavedChanges(false);
    setActiveView('journal');
  };

  const handleSelectEntry = (entry) => {
    if (hasUnsavedChanges) {
      const confirm = window.confirm('You have unsaved changes. Do you want to discard them?');
      if (!confirm) return;
    }
    setSelectedEntry(entry);
    setCurrentEntry({ ...entry });
    setHasUnsavedChanges(false);
    setActiveView('journal');
  };

  const handleEntryChange = (updatedEntry) => {
    setCurrentEntry(updatedEntry);
    setHasUnsavedChanges(true);
  };

  const handleSave = async () => {
    if (!currentEntry) return;

    try {
      const updatedEntries = await saveEntry(currentEntry);
      const sorted = updatedEntries.sort((a, b) =>
        new Date(b.date) - new Date(a.date)
      );
      setEntries(sorted);

      // Re-select the latest saved version (includes mood/weather returned by backend)
      const saved = sorted.find(e => e.id === currentEntry.id) || sorted.find(e => e.date === currentEntry.date);
      if (saved) {
        setSelectedEntry(saved);
        setCurrentEntry({ ...saved });
      } else {
        setSelectedEntry(currentEntry);
      }

      setHasUnsavedChanges(false);
    } catch (error) {
      console.error('Failed to save entry:', error);
      alert('Failed to save entry. Please try again.');
    }
  };

  const handleOpenWeeklySummary = () => {
    if (hasUnsavedChanges) {
      const confirm = window.confirm('You have unsaved changes. Do you want to discard them and view Weekly Summary?');
      if (!confirm) return;
    }
    setHasUnsavedChanges(false);
    setActiveView('weekly');
  };

  const handleCloseWeeklySummary = () => {
    setActiveView('journal');
  };

  const handleToggleTheme = () => {
    setTheme(prev => toggleThemeValue(prev));
  };

  // Auto-save on Ctrl+S
  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        if (hasUnsavedChanges) {
          handleSave();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [hasUnsavedChanges, currentEntry]);

  // Show login page if user is not logged in
  if (!currentUser) {
    return <Login onLogin={handleLogin} />;
  }

  return (
    <div className="flex h-screen bg-dark-bg overflow-hidden">
      <Sidebar
        entries={entries}
        selectedEntry={selectedEntry}
        onSelectEntry={handleSelectEntry}
        onNewEntry={handleNewEntry}
      />

      <div className="flex-1 flex flex-col overflow-hidden">
        <TopBar
          currentEntry={currentEntry}
          currentUser={currentUser}
          onSave={handleSave}
          onLogout={handleLogout}
          hasUnsavedChanges={hasUnsavedChanges}
          onWeeklySummary={handleOpenWeeklySummary}
          activeView={activeView}
          theme={theme}
          onToggleTheme={handleToggleTheme}
        />

        {activeView === 'weekly' ? (
          <WeeklySummary entries={entries} onClose={handleCloseWeeklySummary} />
        ) : (
          <Editor
            entry={currentEntry}
            onChange={handleEntryChange}
          />
        )}
      </div>
    </div>
  );
}

export default App;

