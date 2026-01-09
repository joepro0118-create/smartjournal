import React from 'react';
import { formatDate } from '../storage';

const TopBar = ({
  currentEntry,
  currentUser,
  onSave,
  onLogout,
  hasUnsavedChanges,
  onWeeklySummary,
  activeView,
  theme,
  onToggleTheme,
}) => {
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

  // Simple local greeting based on time of day
  const greeting = (() => {
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 12) return 'Good morning';
    if (hour >= 12 && hour < 17) return 'Good afternoon';
    if (hour >= 17 && hour < 21) return 'Good evening';
    return 'Good night';
  })();

  const isLight = theme === 'light';

  return (
    <div className="bg-dark-surface border-b border-dark-lighter px-6 py-4">
      <div className="flex items-center justify-between gap-4">
        {/* Left: title + date */}
        <div className="min-w-0">
          <h1 className="text-2xl font-bold text-light-text leading-tight truncate">
            Smart Journal
          </h1>
          <p className="text-sm text-light-muted truncate">
            {entryDate ? entryDate : today}
          </p>
        </div>

        {/* Middle: greeting (hero). Between date and user/actions */}
        <div className="flex-1 min-w-0 flex justify-center">
          <h2 className="text-4xl font-extrabold text-light-text leading-tight text-center truncate">
            {greeting}
          </h2>
        </div>

        {/* Right: user + actions */}
        <div className="flex items-center gap-3">
          {currentUser && (
            <div className="text-right mr-2">
              <p className="text-sm text-light-text font-medium">{currentUser.displayName}</p>
              <p className="text-xs text-light-muted">{currentUser.email}</p>
            </div>
          )}

          {/* Theme toggle (bigger switch with label inside) */}
          <button
            type="button"
            onClick={onToggleTheme}
            role="switch"
            aria-checked={isLight}
            title={isLight ? 'Switch to dark mode' : 'Switch to light mode'}
            aria-label={isLight ? 'Switch to dark mode' : 'Switch to light mode'}
            className={`relative inline-flex h-9 w-20 items-center rounded-full transition-colors duration-200 border overflow-hidden ${
              isLight
                ? 'bg-emerald-500/90 border-emerald-400/60'
                : 'bg-dark-lighter border-dark-lighter'
            }`}
          >
            {/* Label text inside the pill */}
            <span
              className={`absolute inset-0 flex items-center text-[11px] font-semibold tracking-wide select-none ${
                isLight ? 'justify-start pl-2 text-white' : 'justify-end pr-2 text-light-muted'
              }`}
            >
              {isLight ? 'Light' : 'Dark'}
            </span>

            {/* Sliding knob */}
            <span
              aria-hidden="true"
              className={`relative inline-block h-8 w-8 transform rounded-full bg-white shadow transition-transform duration-200 ${
                isLight ? 'translate-x-11' : 'translate-x-1'
              }`}
            />
          </button>

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

      {/* Weather + Mood for the selected entry */}
      {(weather || mood) && (
        <div className="mt-3 flex flex-wrap gap-2">
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
  );
};

export default TopBar;
