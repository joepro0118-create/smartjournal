# Smart Journal — Reader Setup & Usage Guide (Windows)

This guide is for someone who **cloned your repo** and wants to run the Smart Journal on their own laptop.

## What you get

- **Backend (Java / Spring Boot)** on `http://localhost:8080`
- **Frontend (React + Vite)** on `http://localhost:5173`
- **Accounts** stored in: `UserData.txt`
- **Journal entries** stored in: `journals/` as `email_YYYY-MM-DD.txt`

---

## 1) Prerequisites (install these first)

### A) Java 21
Spring Boot 3.2.x works best with **Java 17–21** (Java 21 recommended).

- Download and install **JDK 21**.
- After install, confirm in a new terminal:

```powershell
java -version
```

You should see something like `21.x`.

### B) Node.js (LTS)
Needed for the React UI.

Confirm:

```powershell
node -v
npm -v
```

### C) Git
So you can clone the project.

---

## 2) Clone the project

```powershell
git clone <YOUR_REPO_URL>
cd smartjournal
```

---

## 3) Fix JAVA_HOME (Windows)

Many Windows startup issues come from `JAVA_HOME` being wrong.

### A) Set JAVA_HOME to JDK 21 folder
Example path (yours may differ):

`C:\Program Files\Java\jdk-21`

**Important:**
- `JAVA_HOME` must point to the **JDK folder**, not `bin`.
- Don’t wrap it in quotes in Environment Variables.

### B) Add Java to PATH
Ensure your PATH contains:

`%JAVA_HOME%\bin`

### C) Verify Maven uses the correct Java
From the project root:

```powershell
.4mvnw.cmd -v
```

Look for a line like:
- `Java version: 21...`

---

## 4) Run the backend (Spring Boot)

### Option A (Recommended): Run in IntelliJ
1. Open the project in IntelliJ
2. Open `src/main/java/org/example/JournalApplication.java`
3. Right-click → **Run 'JournalApplication.main()'**

Wait for:
- `Started JournalApplication`
- `Tomcat started on port(s): 8080`

### Option B: Run using Maven wrapper
From the project root:

```powershell
.4mvnw.cmd spring-boot:run
```

If you get: `No plugin found for prefix 'spring-boot'`
- You are probably running the command in the wrong folder.
- Make sure you are in the folder that contains `pom.xml`.

---

## 5) Run the frontend (React UI)

Open a **second terminal**:

```powershell
cd web
npm install
npm run dev
```

Then open:
- http://localhost:5173

---

## 6) First-time usage (Register + Login)

### Register
1. Open the website
2. Switch to **Register**
3. Enter:
   - Email
   - Display Name
   - Password
4. Create account

Your account is stored in `UserData.txt`.

### Login
1. Switch to **Login**
2. Enter the same email/password

---

## 7) Writing + reading entries

- Click **New Entry**
- Write your **Title** + journal content
- Click **Save**

Files are saved to:
- `journals/<email>_YYYY-MM-DD.txt`

Mood + Weather should be shown in the UI header when you open an entry.

---

## 8) Common problems & fixes

### A) Backend is running but UI says “Failed to fetch”
This usually means:
- Backend isn’t running, or
- Wrong API URL in frontend, or
- You changed backend port.

Check backend is running:
- http://localhost:8080/api/health (if implemented)

If backend uses another port (example: 8081), update:
- `src/main/resources/application.properties` → `server.port=8081`
- `web/src/api.js` → `http://localhost:8081/api`

### B) "Unsupported class file major version 69"
This means you compiled/running with **Java 25** but dependencies/plugins expect a lower version.
Fix by using **Java 21**:
- Set `JAVA_HOME` to JDK 21
- Restart terminal/IntelliJ
- Re-run.

### C) PowerShell: `cd /d` doesn’t work
In PowerShell, use only:

```powershell
cd "C:\path\to\folder"
```

### D) Ports already in use (8080 / 5173)
- Stop the process using that port, or change ports.

---

## 9) How to run it next time (quick)

1. Start backend (IntelliJ or Maven)
2. Start frontend:

```powershell
cd web
npm run dev
```

Open http://localhost:5173

---

## Notes

- The app stores data locally in text files (`UserData.txt` and `journals/`).
- If you share the project to another laptop, those files start empty unless you copy them too.

