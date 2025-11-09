import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private AuctionState auctionState;

    public ClientHandler(Socket socket, AuctionState auctionState) {
        this.clientSocket = socket;
        this.auctionState = auctionState;
    }

    @Override
    public void run() {
        PrintWriter out = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            AuctionServer.addClient(out);

            out.println("Welcome to the Auction! The current item is " + auctionState.getCurrentItem() + " with a starting price of $" + auctionState.getCurrentPrice());
            out.println("Current highest bidder: " + auctionState.getHighestBidder());
            out.print("Enter your bid amount: > ");
            out.flush();

            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                try {
                    double bidAmount = Double.parseDouble(clientMessage);
                    String bidderName = clientSocket.getInetAddress().getHostAddress();
                    if (auctionState.placeBid(bidAmount, bidderName)) {
                        AuctionServer.broadcast("New highest bid of $" + auctionState.getCurrentPrice() + " from bidder " + auctionState.getHighestBidder());
                        AuctionServer.broadcast("Enter your bid amount: > ");
                    } else {
                        out.println("Bid failed. Your bid must be higher than the current price of $" + auctionState.getCurrentPrice());
                        out.print("Enter your bid amount: > ");
                        out.flush();
                    }
                } catch (NumberFormatException e) {
                    out.println("Invalid bid. Please enter a number.");
                    out.print("Enter your bid amount: > ");
                    out.flush();
                }
            }
        } catch (Exception e) {
            // Handle client disconnection
        } finally {
            if (out != null) {
                AuctionServer.removeClient(out);
            }
            try {
                clientSocket.close();
                System.out.println("Connection with " + clientSocket.getInetAddress().getHostAddress() + " closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
