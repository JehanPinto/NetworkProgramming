# System Flow Diagram

## Application Startup Flow

```
┌─────────────────────────────────────────────────────────────┐
│  User runs: start-backend.ps1 or start-backend.bat         │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │  Compile Java Files   │
         │    javac *.java       │
         └───────────┬───────────┘
                     │
         ┌───────────▼───────────┐
         │  Start AuctionServer  │
         │   Port 5001 (TCP)     │
         │   Port 1099 (RMI)     │
         └───────────┬───────────┘
                     │
         ┌───────────▼────────────┐
         │ Start WebSocketBridge  │
         │    Port 8080 (WS)      │
         └────────────────────────┘
```

## User Interaction Flow

### Admin Flow
```
┌──────────────┐
│ Open Browser │
└──────┬───────┘
       │
       ▼
┌────────────────┐
│ Select "Admin" │
└──────┬─────────┘
       │
       ▼
┌──────────────────────┐
│ Enter Credentials:   │
│ admin / admin123     │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐     ┌─────────────────────────┐
│ Admin Dashboard      │────▶│ Set Timer (10-600 sec)  │
└──────┬───────────────┘     └─────────────┬───────────┘
       │                                    │
       │                     ┌──────────────▼──────────────┐
       │                     │ Click "Start Auction"       │
       │                     └──────────────┬──────────────┘
       │                                    │
       │                     ┌──────────────▼──────────────┐
       └────────────────────▶│ Monitor Bids in Real-time   │
                             │ - View bid history          │
                             │ - See timer countdown       │
                             │ - Track highest bidder      │
                             └─────────────────────────────┘
```

### Bidder Flow
```
┌──────────────┐
│ Open Browser │
└──────┬───────┘
       │
       ▼
┌────────────────┐
│ Select "Bidder"│
└──────┬─────────┘
       │
       ▼
┌─────────────────────────┐
│ Assigned User Number    │
│ (e.g., User-1)          │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│ Wait for Auction Start  │
│ (Bid button disabled)   │
└──────┬──────────────────┘
       │
       ▼ Auction Started
┌─────────────────────────┐
│ View Live Information:  │
│ • Timer countdown       │
│ • Current price         │
│ • Highest bidder        │
│ • Item details          │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│ Enter Bid Amount        │
│ Click "Place Bid"       │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐     ┌──────────────────┐
│ Bid Validation          │────▶│ ✓ Bid Accepted   │
│ • Must be higher        │     │ ✗ Bid Rejected   │
│ • Auction must be open  │     └──────────────────┘
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│ See Updated Status      │
│ • Your bid in history   │
│ • New highest bidder    │
│ • Updated price         │
└──────┬──────────────────┘
       │
       ▼ Timer Expires
┌─────────────────────────┐
│ Auction Ended           │
│ Winner Announced        │
│ (Bid button disabled)   │
└─────────────────────────┘
```

## Communication Flow

### Admin Starting Auction
```
┌──────────┐                  ┌────────────────┐                  ┌──────────────┐
│  Admin   │                  │ WebSocket      │                  │   Auction    │
│ Browser  │                  │    Bridge      │                  │   Server     │
└────┬─────┘                  └───────┬────────┘                  └──────┬───────┘
     │                                │                                   │
     │ START_AUCTION:60              │                                   │
     ├───────────────────────────────▶│                                   │
     │                                │ START_AUCTION:60                  │
     │                                ├──────────────────────────────────▶│
     │                                │                                   │
     │                                │                    startAuction(60)
     │                                │                    Creates timer thread
     │                                │                    Broadcasts to all clients
     │                                │                                   │
     │                                │ AUCTION_STARTED:60                │
     │                                │◀──────────────────────────────────┤
     │ AUCTION_STARTED:60             │                                   │
     │◀───────────────────────────────┤                                   │
     │                                │                                   │
     │                                │ TIMER_UPDATE:59                   │
     │                                │◀──────────────────────────────────┤
     │ TIMER_UPDATE:59                │                                   │
     │◀───────────────────────────────┤                                   │
     │                                │                                   │
     │ (Timer updates every second to all connected clients)              │
     │                                │                                   │
```

### User Placing Bid
```
┌──────────┐                  ┌────────────────┐                  ┌──────────────┐
│  User-1  │                  │ WebSocket      │                  │   Auction    │
│ Browser  │                  │    Bridge      │                  │   Server     │
└────┬─────┘                  └───────┬────────┘                  └──────┬───────┘
     │                                │                                   │
     │ 55.00                          │                                   │
     ├───────────────────────────────▶│                                   │
     │                                │ 55.00                             │
     │                                ├──────────────────────────────────▶│
     │                                │                                   │
     │                                │              placeBid(55.00, "User-1")
     │                                │              Updates auction state
     │                                │              Broadcasts to ALL clients
     │                                │                                   │
     │                                │ BID_UPDATE:User-1:55.00:User-1    │
     │                                │◀──────────────────────────────────┤
     │ BID_UPDATE:User-1:55.00:User-1 │                                   │
     │◀───────────────────────────────┤                                   │
     │                                │                                   │
     │                                ├──────────────────▶ User-2 Browser │
     │                                ├──────────────────▶ User-3 Browser │
     │                                ├──────────────────▶ Admin Browser  │
     │                                │                                   │
     │ (All connected clients receive the update)                         │
     │                                │                                   │
```

## State Diagram

```
                    ┌─────────────────┐
                    │  Server Starts  │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ WAITING FOR     │◀─────┐
                    │ ADMIN TO START  │      │
                    └────────┬────────┘      │
                             │                │
                Admin clicks │                │
                "Start"      │                │
                             ▼                │
                    ┌─────────────────┐      │
                    │ AUCTION RUNNING │      │
                    │ • Timer active  │      │
                    │ • Bids accepted │      │
                    └────────┬────────┘      │
                             │                │
                Timer        │   Admin stops  │
                expires      │   manually     │
                             ▼                │
                    ┌─────────────────┐      │
                    │ AUCTION ENDED   │      │
                    │ • Winner shown  │      │
                    │ • Server stops  │      │
                    └─────────────────┘      │
                                              │
                    (For new auction,         │
                     restart server) ─────────┘
```

## Error Handling Flow

```
User Action                    Validation                  Result
─────────────────────────────────────────────────────────────────
Bid < Current Price     ───▶   Check in                 ───▶ ERROR:
                               AuctionState                  "Bid must be
                                                             higher"

Bid before start        ───▶   Check hasStarted()       ───▶ ERROR:
                                                             "Not started"

Admin wrong password    ───▶   Check credentials        ───▶ ADMIN_AUTH_
                               in ClientHandler              FAILED

Non-numeric bid         ───▶   NumberFormatException    ───▶ ERROR:
                                                             "Invalid amount"

Connection lost         ───▶   Socket closes            ───▶ Reconnect
                                                             required
```

## Technology Stack

```
┌────────────────────────────────────────────────────────┐
│                    Frontend Layer                       │
│  ┌──────────────────────────────────────────────────┐  │
│  │  HTML5 + CSS3 + Vanilla JavaScript               │  │
│  │  • Role-based UI                                 │  │
│  │  • Real-time updates                             │  │
│  │  • Responsive design                             │  │
│  └──────────────────────────────────────────────────┘  │
└──────────────────┬─────────────────────────────────────┘
                   │ WebSocket Protocol
                   │ (ws://localhost:8080)
┌──────────────────▼─────────────────────────────────────┐
│                  Bridge Layer                           │
│  ┌──────────────────────────────────────────────────┐  │
│  │  WebSocketBridge.java                            │  │
│  │  • WebSocket handshake                           │  │
│  │  • Message relay                                 │  │
│  │  • Protocol translation                          │  │
│  └──────────────────────────────────────────────────┘  │
└──────────────────┬─────────────────────────────────────┘
                   │ TCP Socket
                   │ (localhost:5001)
┌──────────────────▼─────────────────────────────────────┐
│                  Backend Layer                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  AuctionServer.java                              │  │
│  │  • Multi-threaded TCP server                     │  │
│  │  • Client management                             │  │
│  │  • Auction lifecycle control                     │  │
│  │                                                  │  │
│  │  ClientHandler.java (per connection)            │  │
│  │  • Authentication                                │  │
│  │  • Message processing                            │  │
│  │  • Bid validation                                │  │
│  │                                                  │  │
│  │  AuctionState.java                               │  │
│  │  • Thread-safe state management                  │  │
│  │  • Bid tracking                                  │  │
│  │  • Timer management                              │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```
