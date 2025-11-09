public class AuctionState {
    private String currentItem;
    private double currentPrice;
    private String highestBidder;
    private boolean isAuctionOpen = false; // Starts as false, admin needs to start it
    private boolean hasStarted = false;
    private int durationSeconds = 60;

    public AuctionState(String item, double startingPrice) {
        this.currentItem = item;
        this.currentPrice = startingPrice;
        this.highestBidder = "No one";
    }

    public synchronized boolean placeBid(double amount, String bidderName) {
        if (!isAuctionOpen || !hasStarted) {
            return false; // Auction is closed or not started
        }
        if (amount > this.currentPrice) {
            this.currentPrice = amount;
            this.highestBidder = bidderName;
            return true;
        }
        return false;
    }

    public synchronized void startAuction() {
        this.isAuctionOpen = true;
        this.hasStarted = true;
    }

    public synchronized void closeAuction() {
        this.isAuctionOpen = false;
    }

    public synchronized boolean isAuctionOpen() {
        return isAuctionOpen;
    }

    public synchronized boolean hasStarted() {
        return hasStarted;
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

    public synchronized void setDuration(int seconds) {
        this.durationSeconds = seconds;
    }

    public synchronized int getDuration() {
        return durationSeconds;
    }

    public synchronized void setStartingPrice(double price) {
        if (!hasStarted) {
            this.currentPrice = price;
        }
    }
}
