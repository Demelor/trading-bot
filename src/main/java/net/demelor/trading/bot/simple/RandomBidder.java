package net.demelor.trading.bot.simple;

import auction.Bidder;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Bidder implementation that just randomly places a bid in range [0, averageBid + 1).
 */
public class RandomBidder implements Bidder {

    private int cash;
    private int averageBid;

    @Override
    public void init(int quantity, int cash) {
        this.cash = cash;
        averageBid = cash / (quantity / 2);
    }

    @Override
    public int placeBid() {
        return Math.min(ThreadLocalRandom.current().nextInt(averageBid * 2 + 1), cash);
    }

    @Override
    public void bids(int own, int other) {
        cash -= own;
    }
}
