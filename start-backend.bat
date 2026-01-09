@echo off
setlocal enabledelayedexpansion

echo Starting Smart Journal Backend (Spring Boot)...
echo.

REM Move to project root reliably
pushd "%~dp0" || (
  echo Error: Could not change directory to project root.
  pause
  exit /b 1
)

REM --- Choose JDK 21 ---
REM Prefer explicit JAVA_HOME if already set; otherwise use common install locations.
if not defined JAVA_HOME (
  if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
  ) else (
    for /d %%D in ("C:\Program Files\Java\jdk-21*") do (
      if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
  )
)

REM Trim surrounding quotes and spaces in JAVA_HOME
if defined JAVA_HOME (
  set "JAVA_HOME=%JAVA_HOME:"=%"
  for /f "tokens=*" %%A in ("%JAVA_HOME%") do set "JAVA_HOME=%%A"
)

if not defined JAVA_HOME (
    echo Error: JAVA_HOME not found and JDK 21 was not detected.
    echo Please set JAVA_HOME to your JDK 21 folder, e.g.
    echo   C:\Program Files\Java\jdk-21
    popd
    pause
    exit /b 1
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Error: JAVA_HOME is set but java.exe was not found:
    echo   JAVA_HOME=%JAVA_HOME%
    echo Fix it to a real JDK 21 folder.
    popd
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Using JAVA_HOME=%JAVA_HOME%
echo.

REM Build runnable Spring Boot jar
set "MAVEN_OPTS="
call mvnw.cmd -q -DskipTests package
if errorlevel 1 (
  echo.
  echo Build failed. See errors above.
  popd
  pause
  exit /b 1
)

REM Run the built jar
set "JAR=target\FOP_ASSIGNMENT-1.0-SNAPSHOT.jar"
if not exist "%JAR%" (
  echo Error: Could not find %JAR%
  popd
  pause
  exit /b 1
)

echo.
echo Running %JAR%...
java -jar "%JAR%"

popd
pause
