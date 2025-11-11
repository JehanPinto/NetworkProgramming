# Quick Reference Guide

## Starting the System

### Windows:
```cmd
# Double-click start-backend.bat in File Explorer
# OR in PowerShell:
.\start-backend.bat

# OR in Command Prompt:
start-backend.bat

# Manual (if script doesn't work)
# Terminal 1:
javac -d build backend\*.java
cd build
java AuctionServer

# Terminal 2:
cd build
java WebSocketBridge
```

## Admin Credentials
- **Username:** admin
- **Password:** admin123

## Using the Web Interface

### As Admin:
1. Open `frontend/index.html`
2. Click "Admin" button
3. Enter credentials (admin/admin123)
4. Set timer duration (10-600 seconds)
5. Click "Start Auction"
6. Monitor bids in real-time

### As User/Bidder:
1. Open `frontend/index.html`
2. Click "Bidder" button
3. Wait for admin to start auction
4. Enter bid amount and click "Place Bid"
5. Watch timer countdown

## Message Protocol (for developers)

### Client to Server:
- `ADMIN_LOGIN:username:password` - Admin authentication
- `START_AUCTION:duration` - Start auction (admin only)
- `REQUEST_USER_NUMBER` - Get assigned user number
- `REQUEST_STATUS` - Get current auction status
- `<number>` - Place a bid

### Server to Client:
- `ADMIN_AUTH_SUCCESS` - Admin login successful
- `ADMIN_AUTH_FAILED` - Admin login failed
- `USER_NUMBER:<number>` - Assigned user number
- `STATUS:<item>:<price>:<bidder>:<started>` - Auction status
- `BID_UPDATE:<user>:<price>:<winner>` - New bid placed
- `AUCTION_STARTED:<duration>` - Auction has started
- `TIMER_UPDATE:<seconds>` - Countdown update
- `AUCTION_ENDED:<winner>:<price>` - Auction finished
- `ERROR:<message>` - Error message

## Ports Used
- **5001** - Auction Server (TCP)
- **8080** - WebSocket Bridge
- **1099** - RMI Registry

## Troubleshooting

### Backend won't start:
1. Check if ports are in use: `netstat -ano | findstr "5001 8080 1099"`
2. Kill processes if needed: `taskkill /PID <pid> /F`
3. Ensure Java is installed: `java -version`

### Frontend won't connect:
1. Verify both servers are running
2. Check browser console (F12) for errors
3. Try a different browser
4. Check firewall settings

### Compilation errors:
```powershell
# Clean and recompile
Remove-Item *.class
javac *.java
```

## Testing Checklist

- [ ] Backend compiles without errors
- [ ] Both servers start successfully
- [ ] Admin can login with correct credentials
- [ ] Admin login fails with wrong credentials
- [ ] Admin can set timer
- [ ] Admin can start auction
- [ ] Users get assigned numbers
- [ ] Users can see timer countdown
- [ ] Users can place bids
- [ ] Bids update in real-time for all users
- [ ] Timer expires and auction ends
- [ ] Winner is announced correctly
