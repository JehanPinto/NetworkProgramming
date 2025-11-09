public class AuctionState {
    private String currentItem;
    private double currentPrice;
    private String highestBidder;

    public AuctionState(String item, double startingPrice) {
        this.currentItem = item;
        this.currentPrice = startingPrice;
        this.highestBidder = "No one";
    }

    public synchronized boolean placeBid(double amount, String bidderName) {
        if (amount > this.currentPrice) {
            this.currentPrice = amount;
            this.highestBidder = bidderName;
            return true;
        }
        return false;
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
