@echo off
setlocal EnableExtensions

echo Starting SmartJournal (backend + frontend)...
echo.

REM Resolve project root (folder containing this .bat)
set "ROOT=%~dp0"

REM --- Choose JDK 21 for Maven/Java ---
REM Prefer explicit JAVA_HOME if already set correctly.
REM Otherwise try common JDK 21 install locations.

if not defined JAVA_HOME (
  if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
  ) else (
    for /d %%D in ("C:\Program Files\Java\jdk-21*") do (
      if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
  )
)

REM Trim leading spaces if present
if defined JAVA_HOME (
  set "JAVA_HOME=%JAVA_HOME:"=%"
  for /f "tokens=* delims= " %%A in ("%JAVA_HOME%") do set "JAVA_HOME=%%A"
)

REM Validate JAVA_HOME
if not defined JAVA_HOME (
  echo Error: JAVA_HOME is not set and JDK 21 was not found under "C:\Program Files\Java".
  echo Please install JDK 21 and set JAVA_HOME to something like:
  echo   C:\Program Files\Java\jdk-21
  echo.
  pause
  exit /b 1
)

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo Error: JAVA_HOME is set but java.exe was not found:
  echo   JAVA_HOME=%JAVA_HOME%
  echo Fix JAVA_HOME to a real JDK 21 folder.
  echo.
  pause
  exit /b 1
)

echo Using JAVA_HOME=%JAVA_HOME%
echo.

REM --- Backend (Spring Boot) ---
REM Use cmd so the window stays open and shows any errors.
start "SmartJournal Backend" cmd /k "set \"JAVA_HOME=%JAVA_HOME%\" && set \"PATH=%JAVA_HOME%\bin;%PATH%\" && cd /d \"%ROOT%\" && call start-backend.bat"

REM --- Frontend (Vite) ---
start "SmartJournal Frontend" powershell -NoExit -ExecutionPolicy Bypass -Command "Set-Location -LiteralPath '%ROOT%web'; if (!(Test-Path node_modules)) { npm install }; npm run dev"

echo.
echo Started both servers in separate windows.
echo Frontend: http://localhost:5173
echo.
echo You can close either window to stop that server.
echo.
endlocal
