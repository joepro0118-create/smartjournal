# Quick Start Guide - Smart Journal Web App

## ❌ Current Problem: Node.js Not Installed

You're seeing this error because **Node.js is not installed** on your computer:
```
npm : The term 'npm' is not recognized...
```

## ✅ Solution: Install Node.js

### Option 1: Download Installer (Recommended)
1. **Download:** https://nodejs.org/
2. **Choose:** LTS version (currently v20.x or v18.x)
3. **Install:** Run the `.msi` installer
4. **Restart:** Close PowerShell completely and open a new window
5. **Verify:** Run `node --version` and `npm --version`

### Option 2: Use Winget (Windows Package Manager)
If you have winget installed, run:
```powershell
winget install OpenJS.NodeJS.LTS
```

## After Installing Node.js

1. **Close and reopen PowerShell** (IMPORTANT!)

2. **Navigate to web folder:**
   ```powershell
   cd "C:\Users\Joe\OneDrive\Documents\GitHub\smartjournal\web"
   ```

3. **Install dependencies:**
   ```powershell
   npm install
   ```

4. **Start the app:**
   ```powershell
   npm run dev
   ```

5. **Open browser:**
   Go to `http://localhost:5173`

## What You'll See

Once running, you'll have a beautiful dark journal app with:
- 🌙 Dark, cozy interface
- 📝 Large writing area
- 💾 Auto-save functionality
- 📋 Sidebar with all your entries
- ⌨️ Keyboard shortcuts (Ctrl+S to save)

## Need Help?

See `SETUP.md` for detailed instructions and troubleshooting.

