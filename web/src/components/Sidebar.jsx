import React from 'react';
import { formatShortDate, generatePreview } from '../storage';

const Sidebar = ({ entries, selectedEntry, onSelectEntry, onNewEntry }) => {
  return (
    <div className="w-80 bg-dark-surface border-r border-dark-lighter flex flex-col h-screen">
      {/* Sidebar Header */}
      <div className="p-4 border-b border-dark-lighter">
        <h2 className="text-lg font-semibold text-light-text mb-3">Your Journals</h2>
        <button
          onClick={onNewEntry}
          className="w-full bg-dark-purple hover:bg-purple-600 text-light-text py-2 px-4 rounded-lg transition-all duration-200 font-medium shadow-lg hover:shadow-purple-500/20"
        >
          + New Entry
        </button>
      </div>

      {/* Entries List */}
      <div className="flex-1 overflow-y-auto">
        {entries.length === 0 ? (
          <div className="p-4 text-center text-light-muted">
            <p className="mb-2">No entries yet</p>
            <p className="text-sm">Start writing your first journal!</p>
          </div>
        ) : (
          <div className="p-2">
            {entries.map((entry) => (
              <div
                key={entry.id}
                onClick={() => onSelectEntry(entry)}
                className={`p-3 mb-2 rounded-lg cursor-pointer transition-all duration-200 ${
                  selectedEntry?.id === entry.id
                    ? 'bg-dark-accent border border-dark-purple shadow-lg'
                    : 'bg-dark-bg hover:bg-dark-lighter border border-transparent'
                }`}
              >
                <h3 className="text-light-text font-medium mb-1 truncate">
                  {entry.title || 'Untitled'}
                </h3>
                <p className="text-xs text-light-muted mb-2">
                  {formatShortDate(entry.date)}
                </p>
                <p className="text-sm text-light-muted line-clamp-2">
                  {generatePreview(entry.content, 80)}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Sidebar;

