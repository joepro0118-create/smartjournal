import React from 'react';
import { formatDate } from '../storage';

const TopBar = ({ currentEntry, currentUser, onSave, onLogout, hasUnsavedChanges, onWeeklySummary, activeView }) => {
  const today = formatDate(new Date().toISOString());
  const entryDate = currentEntry?.date ? formatDate(currentEntry.date) : null;

  const mood = (currentEntry?.mood || '').trim();
  const weather = (currentEntry?.weather || '').trim();

  const moodClasses = (() => {
    const m = mood.toLowerCase();
    if (m.includes('neg')) return 'bg-red-900/30 border-red-800/60 text-red-200';
    if (m.includes('pos')) return 'bg-emerald-900/30 border-emerald-800/60 text-emerald-200';
    if (m) return 'bg-slate-800/40 border-slate-700/60 text-slate-200';
    return 'bg-dark-lighter text-light-muted border-dark-lighter';
  })();

  const canSave = activeView !== 'weekly' && hasUnsavedChanges;

  return (
    <div className="bg-dark-surface border-b border-dark-lighter px-6 py-4 flex items-center justify-between">
      <div>
        <h1 className="text-2xl font-bold text-light-text mb-1">
          Smart Journal
        </h1>
        <p className="text-sm text-light-muted">
          {entryDate ? entryDate : today}
        </p>

        {/* Weather + Mood for the selected entry */}
        {(weather || mood) && (
          <div className="mt-2 flex flex-wrap gap-2">
            {weather && (
              <span className="text-xs px-3 py-1 rounded-full border border-dark-lighter bg-dark-bg text-light-text/90">
                Weather: {weather}
              </span>
            )}
            {mood && (
              <span className={`text-xs px-3 py-1 rounded-full border ${moodClasses}`}>Mood: {mood}</span>
            )}
          </div>
        )}
      </div>

      <div className="flex items-center gap-3">
        {currentUser && (
          <div className="text-right mr-2">
            <p className="text-sm text-light-text font-medium">{currentUser.displayName}</p>
            <p className="text-xs text-light-muted">{currentUser.email}</p>
          </div>
        )}

        <button
          onClick={onWeeklySummary}
          className={`px-4 py-2 rounded-lg font-medium transition-all duration-200 border ${
            activeView === 'weekly'
              ? 'bg-dark-accent border-dark-purple text-light-text'
              : 'bg-dark-lighter hover:bg-dark-accent border-dark-lighter text-light-text'
          }`}
          title="View your last 7 days mood summary"
        >
          Weekly Summary
        </button>

        <button
          onClick={onSave}
          disabled={!canSave}
          className={`px-6 py-2 rounded-lg font-medium transition-all duration-200 ${
            canSave
              ? 'bg-dark-blue hover:bg-blue-600 text-light-text shadow-lg hover:shadow-blue-500/20'
              : 'bg-dark-lighter text-light-muted cursor-not-allowed'
          }`}
        >
          {canSave ? 'Save' : 'Saved'}
        </button>

        <button
          onClick={onLogout}
          className="px-4 py-2 rounded-lg font-medium bg-red-900/30 hover:bg-red-900/50 text-red-300 hover:text-red-200 transition-all duration-200 border border-red-800/50"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default TopBar;
