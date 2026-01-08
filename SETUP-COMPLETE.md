# Smart Journal - Complete Setup Guide

## 🎯 You Now Have a Full-Stack Application!

### Architecture:
- **Backend**: Java Spring Boot REST API (Port 8080)
- **Frontend**: React + Vite Web GUI (Port 5173)
- **Database**: Text files (UserData.txt, journals/*.txt)
- **AI Features**: Mood analysis & Weather data

---

## 🚀 Quick Start (3 Steps)

### Step 1: Start the Backend Server

**Option A: Using IntelliJ IDEA (Recommended)**
1. Open the project in IntelliJ IDEA
2. Right-click on `JournalApplication.java`
3. Click "Run 'JournalApplication.main()'"
4. Wait for "Started JournalApplication" message

**Option B: Using Command Line**
```powershell
# In project root directory
.\start-backend.bat
```

**Option C: Manual Maven Command**
```powershell
.\mvnw.cmd spring-boot:run
```

The backend will start on **http://localhost:8080**

### Step 2: Start the Frontend (Already Running!)

The React frontend should already be running on **http://localhost:5173**

If not, run:
```powershell
cd web
npm run dev
```

### Step 3: Open Your Browser

Go to **http://localhost:5173** and enjoy your journal!

---

## 📱 How to Use

### First Time:
1. Click "Register" tab
2. Enter your email, display name, and password
3. Click "Create Account"
4. Switch to "Login" tab
5. Login with your credentials

### Writing Journals:
1. Click "New Entry" button in sidebar
2. Write your journal entry
3. Click "Save" button
4. **Magic happens**: AI analyzes your mood & fetches weather!
5. Your entry is saved to `journals/youremail_YYYY-MM-DD.txt`

### Viewing Journals:
- All your journals appear in the left sidebar
- Click any entry to view it
- Edit by clicking and modifying the text
- Save changes with the Save button

---

## 🔧 Troubleshooting

### Backend won't start?

**Check Java is installed:**
```powershell
java -version
```
Should show Java 17 or higher.

**Check port 8080 is free:**
```powershell
netstat -ano | findstr :8080
```

**Rebuild the project:**
```powershell
.\mvnw.cmd clean install
```

### Frontend shows connection errors?

1. Make sure backend is running (check http://localhost:8080)
2. Check browser console for errors (F12)
3. Make sure you're accessing http://localhost:5173 (not 127.0.0.1)

### Login fails even with correct password?

- The backend uses the same authentication as your CLI app
- Check `UserData.txt` to see registered users
- Passwords are hashed with SHA-256

---

## 📂 Project Structure

```
smartjournal/
├── src/main/java/org/example/     # Java Backend
│   ├── JournalApplication.java    # Spring Boot main
│   ├── AuthController.java        # Login/Register API
│   ├── JournalController.java     # Journal CRUD API
│   ├── UserManager.java           # User management
│   ├── API.java                   # Mood & Weather AI
│   └── Main.java                  # CLI version
├── web/src/                       # React Frontend
│   ├── api.js                     # Backend API calls
│   ├── App.jsx                    # Main app component
│   ├── components/
│   │   ├── Login.jsx              # Login/Register page
│   │   ├── TopBar.jsx             # App header
│   │   ├── Sidebar.jsx            # Journal list
│   │   └── Editor.jsx             # Journal editor
│   └── storage.js                 # Data management
├── UserData.txt                   # User database
├── journals/                      # Journal entries
├── pom.xml                        # Maven config
└── start-backend.bat              # Quick start script
```

---

## 🌟 Features

### Authentication
✅ User registration with email & password
✅ Secure login with SHA-256 password hashing
✅ Session persistence
✅ Logout functionality

### Journal Management
✅ Create new journal entries
✅ Edit existing entries
✅ View all entries in chronological order
✅ Auto-save with Ctrl+S
✅ Entry preview in sidebar

### AI Features (When Backend is Connected)
✅ **Mood Analysis**: AI determines if your entry is positive, negative, or neutral
✅ **Weather Data**: Real-time weather for Kuala Lumpur
✅ **Auto-tagging**: Each entry tagged with mood & weather

### UI/UX
✅ Dark theme with purple accents
✅ Responsive design
✅ Smooth animations
✅ Real-time save status
✅ Unsaved changes warning

---

## 🔐 Security Notes

- Passwords are hashed with SHA-256 (never stored in plain text)
- Session data stored in browser localStorage
- CORS enabled for localhost:5173 only
- No external database required

---

## 🎨 Customization

### Change API Base URL:
Edit `web/src/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

### Change Backend Port:
Create `src/main/resources/application.properties`:
```properties
server.port=8080
```

### Change Theme Colors:
Edit `web/tailwind.config.js`

---

## 📞 Support

Having issues? Check:
1. Backend is running (http://localhost:8080)
2. Frontend is running (http://localhost:5173)
3. Browser console for errors (F12)
4. Backend console for errors

---

## 🎉 That's It!

You now have a professional full-stack journal application with:
- Beautiful web interface
- AI-powered mood detection
- Real-time weather tracking
- Secure authentication
- File-based persistent storage

Enjoy journaling! 📔✨

