package net.demelor.trading.bot.simple;

import auction.Bidder;

import java.util.concurrent.ThreadLocalRandom;

public class RandomBidder implements Bidder {

    private final int limit;

    private int cash;

    public RandomBidder(int limit) {
        this.limit = limit;
    }

    @Override
    public void init(int quantity, int cash) {
        this.cash = cash;
    }

    @Override
    public int placeBid() {
        return Math.min(ThreadLocalRandom.current().nextInt(limit), cash);
    }

    @Override
    public void bids(int own, int other) {
        cash -= own;
    }
}
