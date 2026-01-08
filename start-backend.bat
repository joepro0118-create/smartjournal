@echo off
echo Starting Smart Journal Backend Server...
echo.
cd /d "%~dp0"

REM Check if Maven wrapper exists
if not exist mvnw.cmd (
    echo Error: Maven wrapper not found!
    echo Please ensure mvnw.cmd exists in the project root.
    pause
    exit /b 1
)

REM Run Spring Boot application
mvnw.cmd spring-boot:run

pause

