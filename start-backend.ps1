# Auction System Backend Startup Script for Windows PowerShell
# This script starts both AuctionServer and WebSocketBridge

Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  STARTING AUCTION SYSTEM BACKEND" -ForegroundColor Yellow
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host ""

# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java found" -ForegroundColor Green
} catch {
    Write-Host "✗ Java not found. Please install Java and add it to PATH." -ForegroundColor Red
    exit 1
}

# Compile all Java files
Write-Host ""
Write-Host "[Step 1/3] Compiling Java files..." -ForegroundColor Yellow
javac -d build backend\*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Compilation successful" -ForegroundColor Green

# Start AuctionServer in a new window
Write-Host ""
Write-Host "[Step 2/3] Starting Auction Server on port 5001..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
    "Write-Host 'AUCTION SERVER' -ForegroundColor Cyan; Set-Location build; java AuctionServer"
Start-Sleep -Seconds 2
Write-Host "✓ Auction Server started" -ForegroundColor Green

# Start WebSocketBridge in a new window
Write-Host ""
Write-Host "[Step 3/3] Starting WebSocket Bridge on port 8080..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
    "Write-Host 'WEBSOCKET BRIDGE' -ForegroundColor Magenta; Set-Location build; java WebSocketBridge"
Start-Sleep -Seconds 1
Write-Host "✓ WebSocket Bridge started" -ForegroundColor Green

Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  BACKEND SERVICES RUNNING" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  Auction Server:    localhost:5001" -ForegroundColor White
Write-Host "  WebSocket Bridge:  localhost:8080" -ForegroundColor White
Write-Host "  RMI Registry:      localhost:1099" -ForegroundColor White
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "To connect:" -ForegroundColor Yellow
Write-Host "  • Open frontend/index.html in your browser" -ForegroundColor White
Write-Host "  • Run 'java AuctionClient' for terminal client" -ForegroundColor White
Write-Host "  • Run 'java AdminConsole' for admin console" -ForegroundColor White
Write-Host ""
Write-Host "Close the server windows to stop the services." -ForegroundColor Yellow
Write-Host ""
