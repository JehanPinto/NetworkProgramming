import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketBridge {

    public static void main(String[] args) throws Exception {
        System.out.println("WebSocket Bridge started on port 8080...");
        try (ServerSocket server = new ServerSocket(8080)) {
            while (true) {
                Socket client = server.accept();
                // Handle each client connection in a new thread
                new Thread(() -> handleWebSocketConnection(client)).start();
            }
        }
    }

    private static void handleWebSocketConnection(Socket client) {
        try {
            System.out.println("Browser connected from: " + client.getInetAddress());
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            Scanner s = new Scanner(in, "UTF-8");

            // --- 1. Perform WebSocket Handshake ---
            String data = s.useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);
            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                String webSocketKey = match.group(1);
                
                // Generate the acceptance key
                String acceptKey = Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-1").digest((webSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8"))
                );

                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n").getBytes("UTF-8");
                
                out.write(response, 0, response.length);
                System.out.println("WebSocket handshake complete.");

                // --- 2. Connect to the main AuctionServer ---
                System.out.println("Connecting to main AuctionServer on port 5001...");
                Socket auctionSocket = new Socket("localhost", 5001);
                System.out.println("Connected to AuctionServer.");

                // --- 3. Create the two-way relay ---
                // Thread 1: Messages from AuctionServer -> Browser
                new Thread(() -> {
                    try (InputStream auctionIn = auctionSocket.getInputStream()) {
                        Scanner auctionScanner = new Scanner(auctionIn, "UTF-8");
                        while(auctionScanner.hasNextLine()) {
                            String line = auctionScanner.nextLine();
                            sendWebSocketMessage(out, line);
                        }
                    } catch (Exception e) {
                        // Connection closed
                    } finally {
                        System.out.println("Connection to AuctionServer lost.");
                    }
                }).start();

                // Thread 2: Messages from Browser -> AuctionServer
                try (OutputStream auctionOut = auctionSocket.getOutputStream()) {
                    while (true) {
                        if (in.available() > 0) {
                            String decodedMessage = readWebSocketMessage(in);
                            auctionOut.write((decodedMessage + "\n").getBytes("UTF-8"));
                            auctionOut.flush();
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.err.println("Error in WebSocket connection: " + e.getMessage());
        } finally {
            try {
                client.close();
                System.out.println("Browser connection closed.");
            } catch (Exception e) { /* ignore */ }
        }
    }

    // Encodes a message into a simple WebSocket text frame
    private static void sendWebSocketMessage(OutputStream out, String message) throws Exception {
        byte[] rawData = message.getBytes("UTF-8");
        int frameCount = 0;
        byte[] frame = new byte[10 + rawData.length];
        frame[0] = (byte) 129; // 0x81 for a final text frame

        if (rawData.length <= 125) {
            frame[1] = (byte) rawData.length;
            frameCount = 2;
        } else {
            // This simple implementation doesn't support larger frames
            System.err.println("Message too long to send");
            return;
        }

        int b = 0;
        for (int i = 0; i < rawData.length; i++) {
            frame[frameCount + i] = rawData[i];
        }
        
        out.write(frame, 0, frameCount + rawData.length);
        out.flush();
    }

    // Decodes a simple WebSocket text frame
    private static String readWebSocketMessage(InputStream in) throws Exception {
        int opcode = in.read();
        if (opcode != 129) { // We only support final text frames (0x81)
            throw new IOException("Unsupported WebSocket frame received.");
        }
        int len = in.read() - 128; // Mask bit is set, so subtract 128
        if (len > 125) {
            throw new IOException("Unsupported frame length.");
        }
        byte[] key = new byte[4];
        in.read(key, 0, 4);

        byte[] encoded = new byte[len];
        in.read(encoded, 0, len);

        byte[] decoded = new byte[len];
        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte) (encoded[i] ^ key[i % 4]);
        }
        return new String(decoded, "UTF-8");
    }
}
