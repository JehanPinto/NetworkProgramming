import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AuctionClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5001)) {
            System.out.println("Connected to the Auction Server.");

            try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                out.println("Hello Server");

                String serverResponse = in.readLine();
                System.out.println("Response from server: " + serverResponse);
            }
            
            System.out.println("Connection closed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
