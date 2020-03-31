package net.demelor.trading.bot.simulation;

import auction.Bidder;
import net.demelor.trading.bot.informed.AboveAverageBidder;
import net.demelor.trading.bot.informed.AdaptingAboveAverageBidder;
import net.demelor.trading.bot.informed.LinearRegressionBidder;
import net.demelor.trading.bot.simple.AverageBidder;
import net.demelor.trading.bot.simple.LinearGrowthBidder;
import net.demelor.trading.bot.simple.ZeroBidder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TradeTest {
    @Test void similarNonRandomBidders_shouldFinishInATie() {
        int quantity = 100;
        int cash = 1000;

        assertTrue(finishedInATie(new Trade(new ZeroBidder(), new ZeroBidder(), quantity, cash).runTrade()));
        assertTrue(finishedInATie(new Trade(new LinearGrowthBidder(), new LinearGrowthBidder(), quantity, cash).runTrade()));
        assertTrue(finishedInATie(new Trade(new AverageBidder(), new AverageBidder(), quantity, cash).runTrade()));
        assertTrue(finishedInATie(new Trade(new AboveAverageBidder(), new AboveAverageBidder(), quantity, cash).runTrade()));
        assertTrue(finishedInATie(new Trade(new AdaptingAboveAverageBidder(), new AdaptingAboveAverageBidder(), quantity, cash).runTrade()));
        assertTrue(finishedInATie(new Trade(new LinearRegressionBidder(), new LinearRegressionBidder(), quantity, cash).runTrade()));
    }

    /**
     * Checks that results are the same with accepted deviation of 10% based on smaller value.
     */
    private static boolean finishedInATie(Trade.TradeResults results) {
        final double acceptableDeviation = 0.1;

        int resultsDelta = Math.abs(results.bidderOneResult - results.bidderTwoResult);
        return resultsDelta < Math.min(results.bidderOneResult, results.bidderTwoResult) * acceptableDeviation;
    }

    @Test void trade_shouldInitializeBidders_andInteractWithThemOnEachRound() {
        int bidderOneBid = 1;
        Bidder bidderOne = mock(Bidder.class);
        when(bidderOne.placeBid()).thenReturn(bidderOneBid);

        int bidderTwoBid = 0;
        Bidder bidderTwo = mock(Bidder.class);
        when(bidderTwo.placeBid()).thenReturn(bidderTwoBid);

        int quantity = 100;
        int cash = 1000;
        int roundsCount = quantity / 2;

        Trade trade = new Trade(bidderOne, bidderTwo, quantity, cash);
        Trade.TradeResults results = trade.runTrade();

        verify(bidderOne, times(1)).init(eq(quantity), eq(cash));
        verify(bidderTwo, times(1)).init(eq(quantity), eq(cash));

        verify(bidderOne, times(roundsCount)).placeBid();
        verify(bidderOne, times(roundsCount)).bids(eq(bidderOneBid), eq(bidderTwoBid));

        verify(bidderTwo, times(roundsCount)).placeBid();
        verify(bidderTwo, times(roundsCount)).bids(eq(bidderTwoBid), eq(bidderOneBid));

        assertEquals(quantity, results.bidderOneResult);
        assertEquals(0, results.bidderTwoResult);
    }

    @Test void trade_shouldThrowOnEvenQuantity_andNonPositiveQuantity() {
        Bidder bidderOne = mock(Bidder.class);
        Bidder bidderTwo = mock(Bidder.class);

        assertDoesNotThrow(() -> new Trade(bidderOne, bidderTwo, 16, 100));

        assertThrows(IllegalArgumentException.class, () -> new Trade(bidderOne, bidderTwo, 15, 100));
        assertThrows(IllegalArgumentException.class, () -> new Trade(bidderOne, bidderTwo, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> new Trade(bidderOne, bidderTwo, -10, 100));
    }

    @Test void trade_shouldThrowOnNegativeCash() {
        Bidder bidderOne = mock(Bidder.class);
        Bidder bidderTwo = mock(Bidder.class);

        assertDoesNotThrow(() -> new Trade(bidderOne, bidderTwo, 16, 0));
        assertDoesNotThrow(() -> new Trade(bidderOne, bidderTwo, 16, 100));

        assertThrows(IllegalArgumentException.class, () -> new Trade(bidderOne, bidderTwo, 15, -10));
    }
}
