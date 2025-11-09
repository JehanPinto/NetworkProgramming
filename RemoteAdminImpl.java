import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * The server-side implementation of the RemoteAdmin interface.
 */
public class RemoteAdminImpl extends UnicastRemoteObject implements RemoteAdmin {
    private AuctionState auctionState;

    public RemoteAdminImpl(AuctionState auctionState) throws RemoteException {
        super();
        this.auctionState = auctionState;
    }

    @Override
    public String stopAuction() throws RemoteException {
        if (auctionState.isAuctionOpen()) {
            System.out.println(">>> Auction stopped remotely by admin. <<<");
            auctionState.closeAuction();
            String winnerMessage = String.format("!!! AUCTION OVER! (Stopped by Admin) Final winner: %s with a bid of $%.2f !!!",
                    auctionState.getHighestBidder(), auctionState.getCurrentPrice());
            AuctionServer.broadcast(winnerMessage);
            AuctionServer.shutdown();
            return "Auction stopped successfully. Winner announced.";
        } else {
            return "Auction was already closed.";
        }
    }

    @Override
    public String getAuctionStatus() throws RemoteException {
        if (!auctionState.isAuctionOpen()) {
            return "Auction is closed. Final winner: " + auctionState.getHighestBidder() + " with $" + auctionState.getCurrentPrice();
        }
        return String.format("Current Item: %s | Current Price: $%.2f | Highest Bidder: %s",
                auctionState.getCurrentItem(), auctionState.getCurrentPrice(), auctionState.getHighestBidder());
    }
}
