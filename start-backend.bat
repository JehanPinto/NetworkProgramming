@echo off
REM Auction System Backend Startup Script for Windows

echo =================================================
echo   STARTING AUCTION SYSTEM BACKEND
echo =================================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found. Please install Java and add it to PATH.
    pause
    exit /b 1
)
echo [OK] Java found

echo.
echo [Step 1/3] Compiling Java files...
javac *.java
if errorlevel 1 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo [OK] Compilation successful

echo.
echo [Step 2/3] Starting Auction Server on port 5001...
start "Auction Server" cmd /k "echo AUCTION SERVER & java AuctionServer"
timeout /t 2 /nobreak >nul
echo [OK] Auction Server started

echo.
echo [Step 3/3] Starting WebSocket Bridge on port 8080...
start "WebSocket Bridge" cmd /k "echo WEBSOCKET BRIDGE & java WebSocketBridge"
timeout /t 1 /nobreak >nul
echo [OK] WebSocket Bridge started

echo.
echo =================================================
echo   BACKEND SERVICES RUNNING
echo =================================================
echo   Auction Server:    localhost:5001
echo   WebSocket Bridge:  localhost:8080
echo   RMI Registry:      localhost:1099
echo =================================================
echo.
echo To connect:
echo   - Open frontend/index.html in your browser
echo   - Run 'java AuctionClient' for terminal client
echo   - Run 'java AdminConsole' for admin console
echo.
echo Close the server windows to stop the services.
echo.
pause
