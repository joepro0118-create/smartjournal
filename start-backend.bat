@echo off
setlocal enabledelayedexpansion

echo Starting Smart Journal Backend Server...
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

REM If PORT is not set, auto-pick a free port from this list
if "%PORT%"=="" (
    for %%p in (8080 8081 8082 8083 8084 8090) do (
        set "_inuse="
        for /f "tokens=*" %%l in ('netstat -ano ^| findstr /R /C:":%%p .*LISTENING"') do set "_inuse=1"
        if not defined _inuse (
            set "PORT=%%p"
            goto :portChosen
        )
    )
)

:portChosen
if "%PORT%"=="" (
    echo Error: Could not find a free port.
    pause
    exit /b 1
)

echo Backend will run on port %PORT%

REM Provide Spring Boot port via env JSON
set "SPRING_APPLICATION_JSON={\"server\":{\"port\":%PORT%}}"

REM Run Spring Boot application
call mvnw.cmd spring-boot:run

pause
