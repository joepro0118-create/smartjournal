import React from 'react';
import { formatDate } from '../storage';

const TopBar = ({ currentEntry, onSave, hasUnsavedChanges }) => {
  const today = formatDate(new Date().toISOString());

  return (
    <div className="bg-dark-surface border-b border-dark-lighter px-6 py-4 flex items-center justify-between">
      <div>
        <h1 className="text-2xl font-bold text-light-text mb-1">
          ✨ Smart Journal
        </h1>
        <p className="text-sm text-light-muted">{today}</p>
      </div>

      <button
        onClick={onSave}
        disabled={!hasUnsavedChanges}
        className={`px-6 py-2 rounded-lg font-medium transition-all duration-200 ${
          hasUnsavedChanges
            ? 'bg-dark-blue hover:bg-blue-600 text-light-text shadow-lg hover:shadow-blue-500/20'
            : 'bg-dark-lighter text-light-muted cursor-not-allowed'
        }`}
      >
        {hasUnsavedChanges ? '💾 Save' : '✓ Saved'}
      </button>
    </div>
  );
};

export default TopBar;

