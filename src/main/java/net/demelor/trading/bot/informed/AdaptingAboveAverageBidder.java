package net.demelor.trading.bot.informed;

import auction.Bidder;

/**
 * Bidder implementation that assumes that other bidder would bid an average amount on each round,
 * based on other bidder's cash left, and tries to overbid it by smallest amount possible.
 * If opponent's bid received differs from expected, tries to correct next bid prediction by calculated difference.
 */
public class AdaptingAboveAverageBidder implements Bidder {

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
