package net.demelor.trading.bot.simple;

import auction.Bidder;

/**
 * Bidder implementation that calculates base bid size from a cash amount divided by trading rounds count and
 * bids that amount plus additional X. X is linearly growing and funded from cash of last 1/3 of rounds.
 */
public class LinearGrowthBidder implements Bidder {

    private int cash;
    private int baseBid;
    private double linearCoefficient;

    private int round = 0;

    @Override
    public void init(int quantity, int cash) {
        this.cash = cash;

        int rounds = quantity / 2;
        int growthRounds = (rounds / 3) * 2;

        baseBid = cash / rounds;
        int growthCash = cash / 3;

        // Using growth cash amount as the area of a triangle to find linear growth coefficient.
        linearCoefficient = 2 * growthCash / Math.pow(growthRounds, 2);
    }

    @Override
    public int placeBid() {
        int growth = (int) Math.round(linearCoefficient * round);
        return Math.min(baseBid + growth, cash);
    }

    @Override
    public void bids(int own, int other) {
        cash -= own;
        round++;
    }
}
