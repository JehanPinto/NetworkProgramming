import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Unified backend launcher that starts both AuctionServer and WebSocketBridge
 * in separate processes with console output.
 */
public class StartBackend {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  STARTING AUCTION SYSTEM BACKEND");
        System.out.println("=================================================");
        
        try {
            // Start AuctionServer
            System.out.println("\n[1/2] Starting Auction Server on port 5001...");
            ProcessBuilder auctionServerBuilder = new ProcessBuilder("java", "AuctionServer");
            auctionServerBuilder.inheritIO(); // Show output in console
            Process auctionServerProcess = auctionServerBuilder.start();
            
            // Give it a moment to start
            Thread.sleep(2000);
            
            if (!auctionServerProcess.isAlive()) {
                System.err.println("ERROR: AuctionServer failed to start!");
                return;
            }
            System.out.println("✓ Auction Server started successfully");
            
            // Start WebSocketBridge
            System.out.println("\n[2/2] Starting WebSocket Bridge on port 8080...");
            ProcessBuilder webSocketBuilder = new ProcessBuilder("java", "WebSocketBridge");
            webSocketBuilder.inheritIO(); // Show output in console
            Process webSocketProcess = webSocketBuilder.start();
            
            // Give it a moment to start
            Thread.sleep(1000);
            
            if (!webSocketProcess.isAlive()) {
                System.err.println("ERROR: WebSocketBridge failed to start!");
                auctionServerProcess.destroy();
                return;
            }
            System.out.println("✓ WebSocket Bridge started successfully");
            
            System.out.println("\n=================================================");
            System.out.println("  BACKEND SERVICES RUNNING");
            System.out.println("=================================================");
            System.out.println("  Auction Server:    localhost:5001");
            System.out.println("  WebSocket Bridge:  localhost:8080");
            System.out.println("  RMI Registry:      localhost:1099");
            System.out.println("=================================================");
            System.out.println("\nPress Ctrl+C to stop all services...\n");
            
            // Add shutdown hook to cleanup processes
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n\nShutting down backend services...");
                auctionServerProcess.destroy();
                webSocketProcess.destroy();
                System.out.println("All services stopped.");
            }));
            
            // Wait for processes
            auctionServerProcess.waitFor();
            webSocketProcess.waitFor();
            
        } catch (Exception e) {
            System.err.println("ERROR starting backend: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
