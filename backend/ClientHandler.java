import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private static int userCounter = 0;
    private Socket clientSocket;
    private AuctionState auctionState;
    private PrintWriter out;
    private String userNumber;
    private boolean isAdmin = false;

    public ClientHandler(Socket socket, AuctionState auctionState) {
        this.clientSocket = socket;
        this.auctionState = auctionState;
        this.userNumber = "User-" + (++userCounter);
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);

            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                
                // Handle admin login
                if (clientMessage.startsWith("ADMIN_LOGIN:")) {
                    String[] parts = clientMessage.split(":");
                    if (parts.length == 3) {
                        String username = parts[1];
                        String password = parts[2];
                        // Hardcoded admin credentials
                        if ("admin".equals(username) && "admin123".equals(password)) {
                            isAdmin = true;
                            out.println("ADMIN_AUTH_SUCCESS");
                            continue;
                        }
                    }
                    out.println("ADMIN_AUTH_FAILED");
                    continue;
                }

                // Handle admin commands
                if (isAdmin && clientMessage.startsWith("START_AUCTION:")) {
                    try {
                        String[] parts = clientMessage.split(":");
                        int duration = Integer.parseInt(parts[1]);
                        double startingPrice = parts.length > 2 ? Double.parseDouble(parts[2]) : auctionState.getCurrentPrice();
                        auctionState.setStartingPrice(startingPrice);
                        AuctionServer.startAuction(duration);
                        out.println("AUCTION_START_SUCCESS");
                    } catch (Exception e) {
                        out.println("AUCTION_START_FAILED");
                    }
                    continue;
                }

                // Handle user registration
                if (clientMessage.equals("REQUEST_USER_NUMBER")) {
                    out.println("USER_NUMBER:" + userNumber);
                    continue;
                }

                // Handle status request
                if (clientMessage.equals("REQUEST_STATUS")) {
                    String status = String.format("STATUS:%s:%.2f:%s:%b",
                        auctionState.getCurrentItem(),
                        auctionState.getCurrentPrice(),
                        auctionState.getHighestBidder(),
                        auctionState.hasStarted());
                    out.println(status);
                    continue;
                }

                // Handle bids
                if (!isAdmin) {
                    try {
                        double bidAmount = Double.parseDouble(clientMessage);
                        if (auctionState.placeBid(bidAmount, userNumber)) {
                            AuctionServer.broadcast("BID_UPDATE:" + userNumber + ":" + auctionState.getCurrentPrice() + ":" + auctionState.getHighestBidder());
                        } else {
                            if (!auctionState.hasStarted()) {
                                out.println("ERROR:Auction has not started yet");
                            } else if (!auctionState.isAuctionOpen()) {
                                out.println("ERROR:Auction is closed");
                            } else {
                                out.println("ERROR:Bid must be higher than $" + auctionState.getCurrentPrice());
                            }
                        }
                    } catch (NumberFormatException e) {
                        out.println("ERROR:Invalid bid amount");
                    }
                }
            }
        } catch (IOException e) {
            // This is expected when the server closes the connection.
            System.out.println("Client handler for " + userNumber + " is closing.");
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
