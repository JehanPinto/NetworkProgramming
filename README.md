# Network Programming Auction

A simple multi-client, timed auction system with a remote admin console, built with Java sockets and RMI.

## How to Run

### 1. Compile
Open a terminal in the project directory and compile all Java files:
```bash
javac *.java
```

### 2. Start the Server
In the same terminal, start the auction server. This will also handle the RMI registry.
```bash
java AuctionServer
```

### 3. Start Clients
Open new, separate terminals for each client.

**To join as a bidder:**
```bash
java AuctionClient
```

**To connect as an administrator:**
```bash
java AdminConsole
```
---
*You can run multiple `AuctionClient` instances to simulate multiple bidders.*
