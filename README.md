# Network Programming Auction System

A comprehensive multi-client auction system with role-based access, real-time bidding, and admin controls.

## Features

✨ **Modern Web Interface** - Beautiful, responsive UI with role selection  
👨‍💼 **Admin Dashboard** - Control auction timing, monitor bids in real-time  
👤 **User Bidding** - Live timer, bid tracking, and instant updates  
🔒 **Secure Admin Access** - Password-protected admin functionality  
⏱️ **Custom Timer** - Admin can set auction duration  
📊 **Real-time Updates** - WebSocket communication for instant notifications  

## Quick Start

### Option 1: Single Command (Recommended)

**Windows PowerShell:**
```powershell
.\start-backend.bat
```

This script will:
1. Compile all Java files
2. Start the Auction Server (port 5001)
3. Start the WebSocket Bridge (port 8080)
4. Display the running status

### Option 2: Manual Start

**Step 1: Compile All Java Code**
```bash
javac *.java
```

**Step 2: Start Backend Services**

Terminal 1 - Auction Server:
```bash
java AuctionServer
```

Terminal 2 - WebSocket Bridge:
```bash
java WebSocketBridge
```

## Connecting to the Auction

### Web Interface (Recommended)

1. Open `frontend/index.html` in your web browser
2. Choose your role:
   - **Admin**: Login with credentials (username: `admin`, password: `admin123`)
   - **Bidder**: Automatically assigned a user number

#### Admin Features:
- Set auction duration (10-600 seconds)
- Start the auction
- Monitor real-time bid updates
- View bid history with timestamps
- See current auction status

#### Bidder Features:
- View assigned user number
- See live countdown timer
- Track current highest bid and bidder
- Place bids in real-time
- View bid history

### Terminal Client (Optional)

**Connect as a bidder:**
```bash
java AuctionClient
```

**Connect as admin:**
```bash
java AdminConsole
```
Commands: `status`, `stop`

## System Architecture

```
┌─────────────────┐         ┌──────────────────┐
│  Web Frontend   │◄───────►│ WebSocket Bridge │
│ (HTML/CSS/JS)   │  :8080  │   (Port 8080)    │
└─────────────────┘         └────────┬─────────┘
                                     │
┌─────────────────┐                  │
│ Terminal Client │◄─────────────────┤
│  AuctionClient  │  :5001           │
└─────────────────┘         ┌────────▼─────────┐
                            │  Auction Server  │
┌─────────────────┐         │   (Port 5001)    │
│  Admin Console  │◄────────┤  + RMI Registry  │
│  (RMI Client)   │  :1099  │   (Port 1099)    │
└─────────────────┘         └──────────────────┘
```

## Default Credentials

**Admin Login:**
- Username: `admin`
- Password: `admin123`

## Port Configuration

- **5001** - Main Auction Server (TCP)
- **8080** - WebSocket Bridge (WebSocket)
- **1099** - RMI Registry (Admin Console)

## Usage Flow

1. **Start Backend** - Run `start-backend.ps1` or manually start both servers
2. **Admin Connects** - Opens web interface, logs in as admin
3. **Admin Sets Timer** - Configures auction duration (e.g., 60 seconds)
4. **Admin Starts Auction** - Clicks "Start Auction" button
5. **Users Join** - Bidders connect and see live countdown
6. **Bidding Begins** - Users place bids, highest bid is tracked
7. **Timer Expires** - Auction ends automatically, winner announced
8. **Alternative End** - Admin can manually stop auction anytime

## Troubleshooting

**"Connection error" in browser:**
- Ensure both backend services are running
- Check that ports 5001 and 8080 are not blocked

**"Java not found" error:**
- Install Java JDK 8 or higher
- Add Java to your system PATH

**Compilation errors:**
- Ensure all `.java` files are in the same directory
- Check Java version compatibility

## Technologies Used

- **Backend**: Java (Socket Programming, RMI, WebSocket)
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Communication**: TCP Sockets, WebSocket Protocol, RMI
- **Architecture**: Multi-threaded Server, Event-driven Client

## 📚 Documentation

For more detailed information, see:
- **[QUICKSTART.md](QUICKSTART.md)** - Quick reference guide with tips and troubleshooting
- **[PROJECT_COMPLETION.md](PROJECT_COMPLETION.md)** - Complete feature list and testing checklist
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Technical implementation details
- **[SYSTEM_FLOW.md](SYSTEM_FLOW.md)** - System architecture and flow diagrams
- **[UI_DOCUMENTATION.md](UI_DOCUMENTATION.md)** - UI design specifications and screenshots
