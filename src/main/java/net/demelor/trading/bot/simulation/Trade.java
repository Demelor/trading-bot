package net.demelor.trading.bot.simulation;

import auction.Bidder;
import org.apache.commons.lang3.Validate;

public class Trade {
    private final Bidder bidderOne;
    private final Bidder bidderTwo;
    private final int quantity;
    private final int cash;

    public Trade(Bidder bidderOne, Bidder bidderTwo, int quantity, int cash) {
        this.bidderOne = bidderOne;
        this.bidderTwo = bidderTwo;
        this.quantity = quantity;
        this.cash = cash;

        Validate.notNull(bidderOne);
        Validate.notNull(bidderTwo);
        Validate.isTrue(quantity % 2 == 0, "Quantity amount cannot be odd value.");
        Validate.isTrue(quantity > 0, "Quantity should be positive non-null value.");
        Validate.isTrue(cash >= 0, "Quantity should be non-negative value.");
    }

    /**
     * Runs new simulation for two bidder implementations with provided quantity and cash amount.
     * @param bidderOne first bidder
     * @param bidderTwo second bidder
     * @param quantity quantity units
     * @param cash cash amount
     * @return results with won quantity amounts for first and second bidders
     */
    public static TradeResults runFor(Bidder bidderOne, Bidder bidderTwo, int quantity, int cash) {
        return new Trade(bidderOne, bidderTwo, quantity, cash).runTrade();
    }

    public TradeResults runTrade() {
        bidderOne.init(quantity, cash);
        bidderTwo.init(quantity, cash);

        int bidderOneCash = cash, bidderTwoCash = cash;
        int runningQuantity = quantity;

        int bidderOneResult = 0, bidderTwoResult = 0;

        while (runningQuantity > 0) {
            runningQuantity -= 2;

            // Accept new bids
            int bidOne = bidderOne.placeBid();
            int bidTwo = bidderTwo.placeBid();

            // Do cash check for bidders
            bidderOneCash -= bidOne;
            bidderTwoCash -= bidTwo;
            if (bidderOneCash < 0 || bidderTwoCash < 0) {
                throw new RuntimeException(String.format("Bidder cash values are invalid: %d %d",
                        bidderOneCash, bidderTwoCash));
            }

            // Decide a winner
            if (bidOne > bidTwo) {
                bidderOneResult += 2;
            } else if (bidTwo > bidOne) {
                bidderTwoResult += 2;
            } else {
                bidderOneResult += 1;
                bidderTwoResult += 1;
            }

            // Notify bidders on round results
            bidderOne.bids(bidOne, bidTwo);
            bidderTwo.bids(bidTwo, bidOne);
        }

        return new TradeResults(bidderOneResult, bidderTwoResult);
    }

    public static final class TradeResults {
        final int bidderOneResult;
        final int bidderTwoResult;

        public TradeResults(int bidderOneResult, int bidderTwoResult) {
            this.bidderOneResult = bidderOneResult;
            this.bidderTwoResult = bidderTwoResult;
        }

        public boolean bidderOneWon() {
            return bidderOneResult > bidderTwoResult;
        }
    }
}
