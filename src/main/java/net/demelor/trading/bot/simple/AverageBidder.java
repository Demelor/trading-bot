package net.demelor.trading.bot.simple;

import auction.Bidder;

public class AverageBidder implements Bidder {

    private int cash;
    private int roundsLeft;

    @Override
    public void init(int quantity, int cash) {
        this.cash = cash;

        roundsLeft = quantity / 2;
    }

    @Override
    public int placeBid() {
        if (roundsLeft > 0) {
            return cash / roundsLeft;
        } else {
            return cash;
        }
    }

    @Override
    public void bids(int own, int other) {
        cash -= own;

        roundsLeft--;
    }
}
