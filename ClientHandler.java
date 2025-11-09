import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private AuctionState auctionState;
    private PrintWriter out;

    public ClientHandler(Socket socket, AuctionState auctionState) {
        this.clientSocket = socket;
        this.auctionState = auctionState;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Welcome to the Auction! The current item is " + auctionState.getCurrentItem() + " with a starting price of $" + auctionState.getCurrentPrice());
            out.println("Current highest bidder: " + auctionState.getHighestBidder());
            out.print("Enter your bid amount: > ");
            out.flush();

            String clientMessage;
            while (auctionState.isAuctionOpen() && (clientMessage = in.readLine()) != null) {
                try {
                    double bidAmount = Double.parseDouble(clientMessage);
                    String bidderName = clientSocket.getInetAddress().getHostAddress();
                    if (auctionState.placeBid(bidAmount, bidderName)) {
                        AuctionServer.broadcast("New highest bid of $" + auctionState.getCurrentPrice() + " from bidder " + auctionState.getHighestBidder());
                        AuctionServer.broadcast("Enter your bid amount: > ");
                    } else {
                        if (!auctionState.isAuctionOpen()) {
                            out.println("Auction is closed. No more bids accepted.");
                        } else {
                            out.println("Bid failed. Your bid must be higher than the current price of $" + auctionState.getCurrentPrice());
                        }
                        out.print("Enter your bid amount: > ");
                        out.flush();
                    }
                } catch (NumberFormatException e) {
                    out.println("Invalid bid. Please enter a number.");
                    out.print("Enter your bid amount: > ");
                    out.flush();
                }
            }
        } catch (IOException e) {
            // This is expected when the server closes the connection.
            System.out.println("Client handler for " + clientSocket.getInetAddress().getHostAddress() + " is closing.");
        } finally {
            AuctionServer.removeClient(this);
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void closeConnection() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
}
