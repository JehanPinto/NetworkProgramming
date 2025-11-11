import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AuctionClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5001)) {
            System.out.println("Connected to the Auction Server.");

            // Thread to read messages from the server and print them
            Thread serverListener = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (Exception e) {
                    // Connection closed or error
                } finally {
                    System.out.println("Connection closed.");
                }
            });
            serverListener.start();

            // Main thread to read user input and send it to the server
            try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
            ) {
                String userInput;
                while (serverListener.isAlive() && (userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                }
            }

        } catch (Exception e) {
            System.out.println("Could not connect to the server. Make sure it's running.");
        }
    }
}
