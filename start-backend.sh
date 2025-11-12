#!/bin/bash
# Auction System Backend Startup Script for macOS/Linux

echo "================================================="
echo "  STARTING AUCTION SYSTEM BACKEND"
echo "================================================="
echo ""

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found. Please install Java."
    exit 1
fi
echo "[OK] Java found"

echo ""
echo "[Step 1/3] Compiling Java files..."
javac -d build backend/*.java
if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed!"
    exit 1
fi
echo "[OK] Compilation successful"

echo ""
echo "[Step 2/3] Starting Auction Server on port 5001..."
osascript -e 'tell app "Terminal" to do script "cd \"'"$(pwd)"'/build\" && echo \"AUCTION SERVER\" && java AuctionServer"'
sleep 2
echo "[OK] Auction Server started"

echo ""
echo "[Step 3/3] Starting WebSocket Bridge on port 8080..."
osascript -e 'tell app "Terminal" to do script "cd \"'"$(pwd)"'/build\" && echo \"WEBSOCKET BRIDGE\" && java WebSocketBridge"'
sleep 1
echo "[OK] WebSocket Bridge started"

echo ""
echo "================================================="
echo "  BACKEND SERVICES RUNNING"
echo "================================================="
echo "  Auction Server:    localhost:5001"
echo "  WebSocket Bridge:  localhost:8080"
echo "  RMI Registry:      localhost:1099"
echo "================================================="
echo ""
echo "To connect:"
echo "  - Open frontend/index.html in your browser"
echo "  - Admin credentials: admin / admin123"
echo ""
echo "Press Ctrl+C to stop this script (servers will keep running)"
echo "To stop servers, close their Terminal windows"
echo ""
