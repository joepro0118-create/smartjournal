@echo off
setlocal EnableExtensions

echo Starting SmartJournal (backend + frontend)...
echo.

REM Resolve project root (folder containing this .bat)
set "ROOT=%~dp0"

REM --- Ensure JAVA_HOME is available for Maven Wrapper ---
REM mvnw.cmd requires JAVA_HOME.
if not defined JAVA_HOME (
  for /f "tokens=2 delims==" %%A in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /c:"java.home"') do set "JAVA_HOME=%%A"
)

REM Trim leading spaces if present
if defined JAVA_HOME (
  for /f "tokens=* delims= " %%A in ("%JAVA_HOME%") do set "JAVA_HOME=%%A"
)

REM If java.home points to ...\jre, go up one level
if defined JAVA_HOME if /i "%JAVA_HOME:~-4%"=="\jre" set "JAVA_HOME=%JAVA_HOME:~0,-4%"

REM Fallback (your machine's known JDK location)
if not defined JAVA_HOME if exist "C:\Program Files\Java\jdk-25\bin\java.exe" (
  set "JAVA_HOME=C:\Program Files\Java\jdk-25"
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
