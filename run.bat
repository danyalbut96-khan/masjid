@echo off
echo ========================================
echo   Masjid Management System - Build
echo ========================================
echo.

REM Create output directory
if not exist "out" mkdir out

REM Create data directory
if not exist "data" mkdir data

REM Compile all Java files
echo Compiling Java files...
javac -d out -encoding UTF-8 src/masjid/model/*.java src/masjid/interfaces/*.java src/masjid/manager/*.java src/masjid/gui/*.java src/masjid/MasjidApp.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo ========================================
echo   Running Masjid Management System...
echo ========================================
echo.

REM Run the application
java -cp out masjid.MasjidApp

pause
