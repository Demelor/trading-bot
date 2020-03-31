package net.demelor.trading.bot.informed;

import auction.Bidder;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * Bidder implementation that tries to overbid by using linear regression analysis on opponent's bids.
 * Bids fixed amount on first 4 rounds and uses regression prediction on next rounds. Uses 1/3 of cash as overbid
 * budget.
 */
public class LinearRegressionBidder implements Bidder {

    private int cash;

    private int averageBid;
    private int overbidPerRound;
    private int round = 0;

    private SimpleRegression regression;

    @Override
    public void init(int quantity, int cash) {
        this.cash = cash;

        int rounds = quantity / 2;
        averageBid = cash / rounds;

        int overbidRounds = (rounds / 3) * 2;
        int overbidBudget = cash / 3;
        overbidPerRound = Math.max(1, overbidBudget / overbidRounds);

        regression = new SimpleRegression();
    }

    @Override
    public int placeBid() {
        int bid;

        if (round < 4) {
            bid = averageBid;
        } else {
            bid = (int) Math.round(regression.predict(round) + overbidPerRound);
        }
        return Math.min(bid, cash);
    }

    @Override
    public void bids(int own, int other) {
        cash -= own;

        regression.addData(round, other);
        round++;
    }
}
