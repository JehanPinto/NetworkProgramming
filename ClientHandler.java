import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientMessage = in.readLine();
            System.out.println("Message from " + clientSocket.getInetAddress().getHostAddress() + ": " + clientMessage);

            out.println("Welcome to the Auction!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connection with " + clientSocket.getInetAddress().getHostAddress() + " closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
