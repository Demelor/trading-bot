package net.demelor.trading.bot.informed;

import auction.Bidder;

public class LinearRegressionBidder implements Bidder {

    private int ownCash;
    private int otherCash;
    private int roundsLeft;

    private int expectedOtherBid;
    private int otherBidCorrection = 0;

    @Override
    public void init(int quantity, int cash) {
        this.ownCash = cash;
        this.otherCash = cash;

        roundsLeft = quantity / 2;
    }

    @Override
    public int placeBid() {
        int bid;

        if (roundsLeft > 0) {
            expectedOtherBid = otherCash / roundsLeft;

            bid = expectedOtherBid + otherBidCorrection + 1;
        } else {
            bid = ownCash;
        }

        return Math.min(bid, ownCash);
    }

    @Override
    public void bids(int own, int other) {
        ownCash -= own;
        otherCash -= other;
        roundsLeft--;

        otherBidCorrection = other - expectedOtherBid;
    }
}
