# Setup Instructions for Smart Journal Web App

## ⚠️ IMPORTANT: You need to install Node.js first!

Node.js is currently **NOT INSTALLED** on your system. npm comes bundled with Node.js.

### Step 1: Install Node.js

1. Go to https://nodejs.org/
2. Download the **LTS (Long Term Support)** version for Windows
3. Run the installer and follow the setup wizard
   - ✅ Check "Automatically install necessary tools" option
   - ✅ Check "Add to PATH" option (should be default)
4. **IMPORTANT:** Close and reopen PowerShell/Terminal after installation
5. Verify installation (see Step 2 below)

### Step 2: Verify Installation

Open PowerShell and run:
```powershell
node --version
npm --version
```

You should see version numbers for both commands.

### Step 3: Install Dependencies

Navigate to the web folder and install dependencies:
```powershell
cd "C:\Users\Joe\OneDrive\Documents\GitHub\smartjournal\web"
npm install
```

### Step 4: Run the Development Server

```powershell
npm run dev
```

The app will start at `http://localhost:5173`

## Project Structure

```
web/
├── src/
│   ├── components/
│   │   ├── Sidebar.jsx      # Left sidebar with entry list
│   │   ├── TopBar.jsx        # Top bar with title and save button
│   │   └── Editor.jsx        # Main text editor
│   ├── App.jsx               # Main app component
│   ├── storage.js            # LocalStorage utilities
│   ├── main.jsx              # Entry point
│   └── index.css             # Global styles + Tailwind
├── index.html
├── package.json
├── vite.config.js
├── tailwind.config.js
└── postcss.config.js
```

## Features Implemented

✅ Dark, cozy UI with purple/blue accents
✅ Left sidebar with journal entry list
✅ Main editor panel for writing
✅ Top bar with app title, date, and save button
✅ LocalStorage persistence
✅ Smooth transitions and hover effects
✅ Responsive layout
✅ Auto-save with Ctrl+S
✅ Entry previews in sidebar
✅ Modular React components

## Color Scheme

- Background: #1a1a2e (dark navy)
- Surface: #16213e (slightly lighter)
- Purple accent: #7c3aed
- Blue accent: #3b82f6
- Text: #e9e9f0 (soft off-white)
- Muted text: #b4b4c8

## Usage

1. Click **"+ New Entry"** to create a new journal
2. Type your title and content
3. Press **Ctrl+S** or click **Save** to save
4. Select entries from the sidebar to view/edit them
5. All data is stored in your browser's LocalStorage

## Troubleshooting

### "npm is not recognized" error
- **Cause:** Node.js is not installed or not in PATH
- **Solution:** 
  1. Install Node.js from https://nodejs.org/
  2. Close and reopen PowerShell
  3. Run `node --version` to verify

### PowerShell script execution errors
- **Cause:** Execution policy blocking scripts
- **Solution:** Run this command in PowerShell (as Administrator):
  ```powershell
  Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
  ```

### Port already in use
- **Cause:** Another app is using port 5173
- **Solution:** 
  - Close other Vite/dev servers, or
  - The app will automatically use a different port

Enjoy your cozy journaling experience! ✨

