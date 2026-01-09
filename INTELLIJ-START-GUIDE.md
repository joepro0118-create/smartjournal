# 🚀 QUICK START - Run Backend from IntelliJ IDEA

## ✅ Why you saw: "Unsupported class file major version 69"

That error means **something is trying to run/read Java 25 bytecode**.

- **Major version 69 = Java 25**
- Spring Boot **does not require Java 25**.
- If your project is compiled/running with Java 25, some tools/plugins can fail.

### Spring Boot 3.2 supports which Java?
- **Minimum:** Java **17**
- **Recommended (best compatibility):** Java **21 (LTS)**

So the fix is: **use JDK 21 for Maven + IntelliJ + Spring Boot**.

---

## 1) What to change in `pom.xml`

Set the Java version to 21:

```xml
<properties>
  <java.version>21</java.version>
</properties>
```

(Already updated in this project.)

---

## 2) Configure `JAVA_HOME` so Maven uses the correct JDK

### Option A (Recommended): Set JAVA_HOME in Windows (permanent)
1. Open **Start Menu** → search **"Edit the system environment variables"**
2. Click **Environment Variables**
3. Under **User variables** click **New** (or Edit if it exists)
4. Set:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-21`
5. Edit `Path` and add:
   - `%JAVA_HOME%\bin`
6. Close terminals and re-open them.

### Option B: Set JAVA_HOME only for one terminal session (temporary)
In PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
java -version
```

---

## 3) IntelliJ setup (so it uses Java 21)

1. IntelliJ → **File → Project Structure**
2. Set:
   - **Project SDK:** Java 21
   - **Project language level:** 21
3. IntelliJ → Settings → **Build Tools → Maven**
4. Set:
   - **JDK for importer:** Java 21
   - **JDK:** Java 21

---

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
- Check Java version: File → Project Structure → Project SDK (**Java 21 recommended**)
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

1. **Install Java 21 (LTS) and set IntelliJ to use it**
2. **Open project in IntelliJ IDEA**
3. **Import Maven dependencies** (auto-prompt)
4. **Right-click `JournalApplication.java` → Run**
5. **Wait for "Started JournalApplication" message**
6. **Open http://localhost:5173 in browser**
7. **Register & Login**
8. **Start journaling!**
