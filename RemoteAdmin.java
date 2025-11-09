import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the remote interface for the auction administrator.
 */
public interface RemoteAdmin extends Remote {
    /**
     * Remotely stops the auction, determines the winner, and notifies clients.
     * @return A confirmation message.
     * @throws RemoteException if a remote communication error occurs.
     */
    String stopAuction() throws RemoteException;

    /**
     * Gets the current status of the auction (item, price, highest bidder).
     * @return A string describing the current auction status.
     * @throws RemoteException if a remote communication error occurs.
     */
    String getAuctionStatus() throws RemoteException;

    /**
     * Starts the auction with a specified duration.
     * @param durationSeconds The duration of the auction in seconds.
     * @return A confirmation message.
     * @throws RemoteException if a remote communication error occurs.
     */
    String startAuction(int durationSeconds) throws RemoteException;
}
