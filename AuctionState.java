public class AuctionState {
    private String currentItem;
    private double currentPrice;
    private String highestBidder;
    private boolean isAuctionOpen = true;

    public AuctionState(String item, double startingPrice) {
        this.currentItem = item;
        this.currentPrice = startingPrice;
        this.highestBidder = "No one";
    }

    public synchronized boolean placeBid(double amount, String bidderName) {
        if (!isAuctionOpen) {
            return false; // Auction is closed
        }
        if (amount > this.currentPrice) {
            this.currentPrice = amount;
            this.highestBidder = bidderName;
            return true;
        }
        return false;
    }

    public synchronized void closeAuction() {
        this.isAuctionOpen = false;
    }

    public synchronized boolean isAuctionOpen() {
        return isAuctionOpen;
    }

    public synchronized String getCurrentItem() {
        return currentItem;
    }

    public synchronized double getCurrentPrice() {
        return currentPrice;
    }

    public synchronized String getHighestBidder() {
        return highestBidder;
    }
}
