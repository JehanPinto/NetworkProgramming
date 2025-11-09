import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionServer {

    private static List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5001)) {
            System.out.println("Auction Server started. Waiting for clients...");
            AuctionState auctionState = new AuctionState("Vintage Painting", 50.0);

            // --- THIS IS THE TIMER THREAD THAT WAS MISSING ---
            new Thread(() -> {
                try {
                    System.out.println("Auction timer started for 60 seconds.");
                    Thread.sleep(60 * 1000); // 60 seconds

                    System.out.println("Timer finished. Closing auction.");
                    auctionState.closeAuction();
                    String winnerMessage = String.format("!!! AUCTION OVER! Final winner: %s with a bid of $%.2f !!!",
                            auctionState.getHighestBidder(), auctionState.getCurrentPrice());
                    broadcast(winnerMessage);
                    shutdown(); // Close all client connections
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }).start();
            // --- END OF TIMER THREAD ---

            while (auctionState.isAuctionOpen()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                    ClientHandler clientHandler = new ClientHandler(clientSocket, auctionState);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                } catch (Exception e) {
                    if (!auctionState.isAuctionOpen()) {
                        System.out.println("Server socket closed, auction is over.");
                        break;
                    }
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server shutting down.");
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
        for (ClientHandler handler : clientHandlers) {
            handler.closeConnection();
        }
        clientHandlers.clear();
    }
}
