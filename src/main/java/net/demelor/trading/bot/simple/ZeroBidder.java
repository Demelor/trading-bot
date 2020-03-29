package net.demelor.trading.bot.simple;

import auction.Bidder;

public class ZeroBidder implements Bidder {
    @Override
    public void init(int quantity, int cash) {

    }

    @Override
    public int placeBid() {
        return 0;
    }

    @Override
    public void bids(int own, int other) {

    }
}
