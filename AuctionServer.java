import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionServer {

    private static List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
    private static volatile boolean auctionRunning = true;
    private static Thread timerThread = null;
    private static AuctionState auctionState;

    public static void main(String[] args) {
        try {
            auctionState = new AuctionState("Vintage Painting", 50.0);
            // Auction starts in PENDING state, waiting for admin to start

            // --- RMI SETUP ---
            try {
                RemoteAdminImpl adminImpl = new RemoteAdminImpl(auctionState);
                Registry registry = LocateRegistry.createRegistry(1099); // Default RMI port
                registry.bind("AuctionAdmin", adminImpl);
                System.out.println("RMI Admin Console registered.");
            } catch (Exception e) {
                System.err.println("RMI setup failed: " + e.toString());
                e.printStackTrace();
            }
            // --- END RMI SETUP ---

            // --- MAIN SERVER LOOP TO ACCEPT CLIENTS ---
            try (ServerSocket serverSocket = new ServerSocket(5001)) {
                System.out.println("Auction Server started. Waiting for clients...");
                while (auctionRunning) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                    ClientHandler clientHandler = new ClientHandler(clientSocket, auctionState);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            }

        } catch (Exception e) {
            if (!auctionRunning) {
                System.out.println("Server is shutting down.");
            } else {
                e.printStackTrace();
            }
        } finally {
            System.out.println("Server has shut down.");
        }
    }

    public static void removeClient(ClientHandler handler) {
        clientHandlers.remove(handler);
    }

    public static void broadcast(String message) {
        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(message);
        }
    }

    public static void shutdown() {
        auctionRunning = false;
        for (ClientHandler handler : clientHandlers) {
            handler.closeConnection();
        }
        clientHandlers.clear();
        // This is a forceful way to exit, ensuring the server socket closes.
        // A more graceful shutdown might involve interrupting the main thread.
        System.exit(0);
    }

    public static void startAuction(int durationSeconds) {
        if (timerThread != null && timerThread.isAlive()) {
            System.out.println("Auction is already running!");
            return;
        }

        auctionState.startAuction();
        auctionState.setDuration(durationSeconds);
        
        System.out.println("Admin started the auction for " + durationSeconds + " seconds.");
        broadcast("AUCTION_STARTED:" + durationSeconds);
        
        // --- AUCTION TIMER THREAD ---
        timerThread = new Thread(() -> {
            try {
                long endTime = System.currentTimeMillis() + (durationSeconds * 1000L);
                
                while (System.currentTimeMillis() < endTime && auctionState.isAuctionOpen()) {
                    long remainingSeconds = (endTime - System.currentTimeMillis()) / 1000;
                    broadcast("TIMER_UPDATE:" + remainingSeconds);
                    Thread.sleep(1000);
                }

                if (auctionState.isAuctionOpen()) {
                    System.out.println("Timer finished. Closing auction.");
                    auctionState.closeAuction();
                    String winnerMessage = String.format("!!! AUCTION OVER! Final winner: %s with a bid of $%.2f !!!",
                            auctionState.getHighestBidder(), auctionState.getCurrentPrice());
                    broadcast("AUCTION_ENDED:" + auctionState.getHighestBidder() + ":" + auctionState.getCurrentPrice());
                    Thread.sleep(3000);
                    shutdown();
                }
            } catch (InterruptedException e) {
                System.out.println("Auction timer interrupted.");
            }
        });
        timerThread.start();
    }

    public static AuctionState getAuctionState() {
        return auctionState;
    }
}
