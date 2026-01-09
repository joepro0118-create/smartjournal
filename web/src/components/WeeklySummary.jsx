import React, { useMemo } from 'react';

const normalizeMood = (mood) => {
  const m = (mood || '').toString().trim().toLowerCase();
  if (!m) return 'unknown';
  if (m.includes('neg')) return 'negative';
  if (m.includes('pos')) return 'positive';
  if (m.includes('neutral')) return 'neutral';
  return 'other';
};

const badgeClasses = (key) => {
  switch (key) {
    case 'positive':
      return 'bg-emerald-900/30 border-emerald-800/60 text-emerald-200';
    case 'negative':
      return 'bg-red-900/30 border-red-800/60 text-red-200';
    case 'neutral':
      return 'bg-slate-800/40 border-slate-700/60 text-slate-200';
    case 'unknown':
      return 'bg-dark-lighter border-dark-lighter text-light-muted';
    default:
      return 'bg-indigo-900/20 border-indigo-800/50 text-indigo-200';
  }
};

const startOfDay = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate());

const WeeklySummary = ({ entries, onClose }) => {
  const summary = useMemo(() => {
    const now = new Date();
    const start = startOfDay(new Date(now));
    start.setDate(start.getDate() - 6); // last 7 days inclusive

    const inRange = entries.filter(e => {
      const d = new Date(e.date);
      return d >= start && d <= now;
    });

    const counts = { positive: 0, negative: 0, neutral: 0, unknown: 0, other: 0 };
    for (const e of inRange) {
      counts[normalizeMood(e.mood)]++;
    }

    // Mood per day (for a simple “trend” list)
    const byDay = new Map();
    for (const e of inRange) {
      const key = new Date(e.date).toISOString().split('T')[0];
      const moodKey = normalizeMood(e.mood);
      if (!byDay.has(key)) byDay.set(key, []);
      byDay.get(key).push({ ...e, moodKey });
    }
    const days = Array.from(byDay.entries())
      .sort((a, b) => b[0].localeCompare(a[0]))
      .map(([date, list]) => ({ date, list }));

    return { start, now, inRange, counts, days };
  }, [entries]);

  return (
    <div className="flex-1 bg-dark-bg overflow-auto">
      <div className="max-w-5xl mx-auto p-6">
        <div className="flex items-start justify-between mb-6">
          <div>
            <h2 className="text-3xl font-bold text-light-text">Weekly Summary</h2>
            <p className="text-sm text-light-muted mt-1">
              Last 7 days • {summary.inRange.length} entr{summary.inRange.length === 1 ? 'y' : 'ies'}
            </p>
          </div>

          <button
            onClick={onClose}
            className="px-4 py-2 rounded-lg font-medium bg-dark-lighter hover:bg-dark-accent text-light-text transition-all duration-200 border border-dark-lighter"
          >
            Back to Journal
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="rounded-xl border border-dark-lighter bg-dark-surface p-5">
            <h3 className="text-lg font-semibold text-light-text mb-3">Mood breakdown</h3>
            <div className="flex flex-wrap gap-2">
              {Object.entries(summary.counts).map(([key, count]) => (
                <span key={key} className={`text-xs px-3 py-1 rounded-full border ${badgeClasses(key)}`}>
                  {key.toUpperCase()}: {count}
                </span>
              ))}
            </div>

            <div className="mt-4 text-sm text-light-muted">
              Tip: moods come from the backend analysis. If an entry is missing mood, it’ll show as UNKNOWN.
            </div>
          </div>

          <div className="rounded-xl border border-dark-lighter bg-dark-surface p-5">
            <h3 className="text-lg font-semibold text-light-text mb-3">Recent days</h3>
            {summary.days.length === 0 ? (
              <p className="text-light-muted">No entries in the last 7 days.</p>
            ) : (
              <div className="space-y-3">
                {summary.days.map(day => (
                  <div key={day.date} className="rounded-lg border border-dark-lighter bg-dark-bg p-3">
                    <div className="flex items-center justify-between">
                      <p className="text-light-text font-medium">{day.date}</p>
                      <p className="text-xs text-light-muted">{day.list.length} entr{day.list.length === 1 ? 'y' : 'ies'}</p>
                    </div>
                    <div className="mt-2 flex flex-wrap gap-2">
                      {day.list.slice(0, 4).map(e => (
                        <span key={e.id} className={`text-[10px] px-2 py-0.5 rounded-full border ${badgeClasses(e.moodKey)}`}>
                          {(e.title || 'Untitled').toString().trim() || 'Untitled'}
                        </span>
                      ))}
                      {day.list.length > 4 && (
                        <span className="text-[10px] px-2 py-0.5 rounded-full border border-dark-lighter bg-dark-surface text-light-muted">
                          +{day.list.length - 4} more
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default WeeklySummary;

