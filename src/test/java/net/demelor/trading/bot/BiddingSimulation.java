package net.demelor.trading.bot;

import auction.Bidder;
import net.demelor.trading.bot.informed.AdaptingAverageBidder;
import net.demelor.trading.bot.informed.InformedAverageBidder;

public class BiddingSimulation {
    private final Bidder bidderOne;
    private final Bidder bidderTwo;
    private final int initialQuantity;

    public static void main(String[] args) {
        BiddingSimulation environment = new BiddingSimulation(new InformedAverageBidder(), new AdaptingAverageBidder(), 200);
        SingleRunResults results = environment.runEqualFundsTradeSimulation(100000);
        System.out.println(String.format("Result: one - %d, two - %d",
                results.bidderOneResult, results.bidderTwoResult));
    }

    public BiddingSimulation(Bidder bidderOne, Bidder bidderTwo, int initialQuantity) {
        this.bidderOne = bidderOne;
        this.bidderTwo = bidderTwo;
        this.initialQuantity = initialQuantity;
    }

    public SingleRunResults runEqualFundsTradeSimulation(int cash) {
        bidderOne.init(initialQuantity, cash);
        bidderTwo.init(initialQuantity, cash);

        int bidderOneCash = cash, bidderTwoCash = cash;
        int runningQuantity = initialQuantity;

        int bidderOneResult = 0, bidderTwoResult = 0;

        while (runningQuantity > 0) {
            int quantity = Math.min(runningQuantity, 2);
            runningQuantity -= quantity;

            int bidOne = bidderOne.placeBid();
            int bidTwo = bidderTwo.placeBid();

            bidderOneCash -= bidOne;
            bidderTwoCash -= bidTwo;
            if (bidderOneCash < 0 || bidderTwoCash < 0) {
                throw new RuntimeException(String.format("Bidder cash values are invalid: %d %d",
                        bidderOneCash, bidderTwoCash));
            }

            if (bidOne > bidTwo) {
                bidderOneResult += quantity;
            } else if (bidTwo > bidOne) {
                bidderTwoResult += quantity;
            } else {
                bidderOneResult += 1;
                bidderTwoResult += 1;
            }

            bidderOne.bids(bidOne, bidTwo);
            bidderTwo.bids(bidTwo, bidOne);
        }

        return new SingleRunResults(bidderOneResult, bidderTwoResult);
    }

    private static final class SingleRunResults {
        final int bidderOneResult;
        final int bidderTwoResult;

        public SingleRunResults(int bidderOneResult, int bidderTwoResult) {
            this.bidderOneResult = bidderOneResult;
            this.bidderTwoResult = bidderTwoResult;
        }
    }
}
