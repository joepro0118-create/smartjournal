import React, { useState, useEffect } from 'react';

const Editor = ({ entry, onChange }) => {
  const [title, setTitle] = useState(entry?.title || '');
  const [content, setContent] = useState(entry?.content || '');

  useEffect(() => {
    setTitle(entry?.title || '');
    setContent(entry?.content || '');
  }, [entry?.id]);

  const handleTitleChange = (e) => {
    const newTitle = e.target.value;
    setTitle(newTitle);
    onChange({ ...entry, title: newTitle });
  };

  const handleContentChange = (e) => {
    const newContent = e.target.value;
    setContent(newContent);
    onChange({ ...entry, content: newContent });
  };

  if (!entry) {
    return (
      <div className="flex-1 flex items-center justify-center bg-dark-bg">
        <div className="text-center">
          <p className="text-2xl text-light-muted mb-2">📔</p>
          <p className="text-light-muted">Select an entry or create a new one to start writing</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col bg-dark-bg overflow-hidden">
      {/* Title Input */}
      <div className="p-6 pb-0">
        <input
          type="text"
          value={title}
          onChange={handleTitleChange}
          placeholder="Entry Title..."
          className="w-full bg-transparent text-3xl font-bold text-light-text placeholder-light-muted focus:outline-none border-b-2 border-transparent focus:border-dark-purple pb-2 transition-colors duration-200"
        />
      </div>

      {/* Content Textarea */}
      <div className="flex-1 p-6 overflow-hidden">
        <textarea
          value={content}
          onChange={handleContentChange}
          placeholder="Start writing your thoughts..."
          className="w-full h-full bg-transparent text-light-text placeholder-light-muted text-lg leading-relaxed focus:outline-none resize-none"
          style={{ minHeight: '100%' }}
        />
      </div>
    </div>
  );
};

export default Editor;

