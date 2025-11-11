import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * A command-line client for administrators to manage the auction via RMI.
 */
public class AdminConsole {
    public static void main(String[] args) {
        try {
            // Connect to the RMI registry on localhost
            Registry registry = LocateRegistry.getRegistry("localhost");
            RemoteAdmin admin = (RemoteAdmin) registry.lookup("AuctionAdmin");
            System.out.println("Admin Console connected successfully.");
            System.out.println("Commands: 'status', 'stop', 'exit'");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("admin> ");
                String command = scanner.nextLine().trim().toLowerCase();

                if ("exit".equals(command)) {
                    break;
                }

                switch (command) {
                    case "status":
                        System.out.println("Status: " + admin.getAuctionStatus());
                        break;
                    case "stop":
                        System.out.println("Stopping auction... " + admin.stopAuction());
                        break;
                    default:
                        System.out.println("Unknown command. Available commands: 'status', 'stop', 'exit'");
                        break;
                }
            }
            scanner.close();
            System.out.println("Admin Console disconnected.");

        } catch (Exception e) {
            System.err.println("Admin Console exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
