# 🚀 QUICK START - Run Backend from IntelliJ IDEA

## The Easiest Way (Recommended)

Since Maven/Java setup can be tricky from command line, here's the easiest way to get your backend running:

### Step 1: Open IntelliJ IDEA
1. If not already open, launch **IntelliJ IDEA**
2. Open your project: `C:\Users\Joe\OneDrive\Documents\GitHub\smartjournal`

### Step 2: Let IntelliJ Download Dependencies
1. When you open the project, IntelliJ will detect `pom.xml`
2. You'll see a notification: **"Maven projects need to be imported"**
3. Click **"Import Changes"** or **"Enable Auto-Import"**
4. Wait for IntelliJ to download all Spring Boot dependencies (this may take a few minutes)
5. Watch the bottom right corner - wait until the progress bar completes

### Step 3: Run the Backend
1. In the Project panel (left side), navigate to:
   ```
   src → main → java → org → example → JournalApplication
   ```
2. Right-click on **`JournalApplication.java`**
3. Click **"Run 'JournalApplication.main()'"**
4. Wait for the console to show: **"Started JournalApplication in X.XXX seconds"**

### Step 4: Verify It's Running
You should see in the console:
```
Tomcat started on port(s): 8080 (http)
Started JournalApplication
```

The backend is now running at **http://localhost:8080**! ✅

---

## Step 5: Test the Connection

### Open your React app:
The React frontend should already be running at **http://localhost:5173**

If not, open a **new terminal** (not in IntelliJ) and run:
```powershell
cd C:\Users\Joe\OneDrive\Documents\GitHub\smartjournal\web
npm run dev
```

### Try it out:
1. Go to **http://localhost:5173** in your browser
2. Click **"Register"**
3. Enter:
   - Email: `test@example.com`
   - Display Name: `Test User`
   - Password: `password123`
4. Click **"Create Account"**
5. Switch to **"Login"** tab
6. Login with the same credentials
7. You should see the journal dashboard!

---

## ✅ Success Indicators

### Backend Running Successfully:
- ✅ Console shows "Started JournalApplication"
- ✅ Console shows "Tomcat started on port(s): 8080"
- ✅ No red error messages in console

### Frontend Connected:
- ✅ You can login successfully
- ✅ When you save a journal, you see mood & weather data
- ✅ Your journal saves to `journals/` folder
- ✅ No "Failed to connect" errors

---

## 🔍 Troubleshooting

### "Cannot resolve symbol 'springframework'" errors?
- Wait for Maven import to complete
- Right-click `pom.xml` → Maven → Reload Project

### Port 8080 already in use?
- Stop any other applications using port 8080
- Or change the port in `src/main/resources/application.properties`:
  ```properties
  server.port=8081
  ```
  Then update `web/src/api.js` to use 8081

### Build fails?
- Check Java version: File → Project Structure → Project SDK (should be Java 17+)
- Try: Maven panel (right side) → Lifecycle → clean → install

---

## 🎯 What Happens When Connected?

### Before (Without Backend):
- ❌ No mood analysis
- ❌ No weather data
- ❌ Data only in browser
- ❌ No sync with CLI app

### After (With Backend):
- ✅ AI analyzes your journal mood
- ✅ Real-time weather saved with each entry
- ✅ Data persists in files
- ✅ Same data accessible from CLI app
- ✅ Secure password hashing

---

## 📝 Quick Recap

1. **Open project in IntelliJ IDEA**
2. **Import Maven dependencies** (auto-prompt)
3. **Right-click `JournalApplication.java` → Run**
4. **Wait for "Started JournalApplication" message**
5. **Open http://localhost:5173 in browser**
6. **Register & Login**
7. **Start journaling!** 🎉

---

That's it! Your full-stack Smart Journal is now running! 📔✨

