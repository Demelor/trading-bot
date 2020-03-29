package net.demelor.trading.bot.simple;

import auction.Bidder;

public class GrowthBidder implements Bidder {

    private int cash;
    private int averageBid;

    private int growth = 0;

    @Override
    public void init(int quantity, int cash) {
        this.cash = cash;

        averageBid = cash / (quantity / 2);
    }

    @Override
    public int placeBid() {
        return Math.min(averageBid + growth, cash);
    }

    @Override
    public void bids(int own, int other) {
        cash -= own;

        growth += 2;
    }
}
