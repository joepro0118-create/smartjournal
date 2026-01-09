@echo off
setlocal enabledelayedexpansion

echo Starting Smart Journal Backend (Console Java App)...
echo.
cd /d "%~dp0"

REM Set JAVA_HOME if not already set
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-25" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-25"
    )
)

if "%JAVA_HOME%"=="" (
    echo Error: JAVA_HOME not found in your environment.
    echo Please install a JDK and set JAVA_HOME.
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Build jar
call mvnw.cmd -q -DskipTests package
if errorlevel 1 (
    echo.
    echo Build failed. See errors above.
    pause
    exit /b 1
)

REM Run jar (console program)
set "JAR=target\FOP_ASSIGNMENT-1.0-SNAPSHOT.jar"
if not exist "%JAR%" (
    echo Error: Jar not found at %JAR%
    echo Try running: mvnw.cmd package
    pause
    exit /b 1
)

echo.
echo Running %JAR%...
java -jar "%JAR%"

pause
