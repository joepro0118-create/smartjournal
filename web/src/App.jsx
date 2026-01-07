import React, { useState, useEffect } from 'react';
import Sidebar from './components/Sidebar';
import TopBar from './components/TopBar';
import Editor from './components/Editor';
import { getEntries, saveEntry, createNewEntry } from './storage';

function App() {
  const [entries, setEntries] = useState([]);
  const [selectedEntry, setSelectedEntry] = useState(null);
  const [currentEntry, setCurrentEntry] = useState(null);
  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);

  // Load entries on mount
  useEffect(() => {
    const loadedEntries = getEntries();
    // Sort by date, newest first
    const sorted = loadedEntries.sort((a, b) =>
      new Date(b.date) - new Date(a.date)
    );
    setEntries(sorted);
  }, []);

  const handleNewEntry = () => {
    const newEntry = createNewEntry();
    setCurrentEntry(newEntry);
    setSelectedEntry(newEntry);
    setHasUnsavedChanges(false);
  };

  const handleSelectEntry = (entry) => {
    if (hasUnsavedChanges) {
      const confirm = window.confirm('You have unsaved changes. Do you want to discard them?');
      if (!confirm) return;
    }
    setSelectedEntry(entry);
    setCurrentEntry({ ...entry });
    setHasUnsavedChanges(false);
  };

  const handleEntryChange = (updatedEntry) => {
    setCurrentEntry(updatedEntry);
    setHasUnsavedChanges(true);
  };

  const handleSave = () => {
    if (!currentEntry) return;

    const updatedEntries = saveEntry(currentEntry);
    const sorted = updatedEntries.sort((a, b) =>
      new Date(b.date) - new Date(a.date)
    );
    setEntries(sorted);
    setSelectedEntry(currentEntry);
    setHasUnsavedChanges(false);
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
          onSave={handleSave}
          hasUnsavedChanges={hasUnsavedChanges}
        />

        <Editor
          entry={currentEntry}
          onChange={handleEntryChange}
        />
      </div>
    </div>
  );
}

export default App;

