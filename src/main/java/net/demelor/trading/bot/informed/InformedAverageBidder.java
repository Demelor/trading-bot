package net.demelor.trading.bot.informed;

import auction.Bidder;

/**
 * Bidder implementation that assumes that other bidder would bid an average amount on each round,
 * based on other bidders cash left, and tries to overbid it by smallest value possible.
 */
public class InformedAverageBidder implements Bidder {

    private int ownCash;
    private int otherCash;
    private int roundsLeft;

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
            int expectedBid = otherCash / roundsLeft;

            bid = expectedBid + 1;
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
    }
}
