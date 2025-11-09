# Network Programming Auction

A multi-client auction system with a TCP-based client, a web-based client, and a remote admin console.

## How to Run

### 1. Compile All Java Code
Open a terminal in the project's root directory and run:
```bash
javac *.java
```

### 2. Start the Backend Services
You need to start two separate server processes in two separate terminals.

**Terminal 1: Start the Main Auction Server**
```bash
java AuctionServer
```
*This handles the core auction logic and the RMI admin service.*

**Terminal 2: Start the WebSocket Bridge**
```bash
java WebSocketBridge
```
*This allows the web frontend to communicate with the main server.*

### 3. Connect Clients
You can now connect any combination of the following clients.

**Option A: Connect a Terminal Bidder**
Open a new terminal and run:
```bash
java AuctionClient
```

**Option B: Connect the Web Frontend**
Open the `frontend/index.html` file in your web browser.

**Option C: Connect the Admin Console**
Open a new terminal and run:
```bash
java AdminConsole
```
*Use `status` to check the current bid or `stop` to end the auction.*
