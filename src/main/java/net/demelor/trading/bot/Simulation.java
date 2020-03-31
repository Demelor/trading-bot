package net.demelor.trading.bot;

import auction.Bidder;
import net.demelor.trading.bot.informed.AboveAverageBidder;
import net.demelor.trading.bot.informed.AdaptingAboveAverageBidder;
import net.demelor.trading.bot.informed.LinearRegressionBidder;
import net.demelor.trading.bot.simple.*;
import net.demelor.trading.bot.simulation.Trade;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Simulation {
    private static final int TRADES_COUNT = 100;

    private static final int ROUNDS_MIN = 10;
    private static final int ROUNDS_MAX = 50;

    private static final int CASH_MIN = 100;
    private static final int CASH_MAX = 2000;

    public static void main(String[] args) {
        Simulation.runFor(
                ZeroBidder.class,
                RandomBidder.class,
                LinearGrowthBidder.class,
                AverageBidder.class,
                AboveAverageBidder.class,
                AdaptingAboveAverageBidder.class,
                LinearRegressionBidder.class);
    }

    /**
     * Evaluates bidder efficiency by placing each bidder class against all others in 1-on-1 trading simulations.
     * Victory counts are printed on STD OUT.
     *
     * @param bidders a variety of classes to run simulation on
     */
    @SafeVarargs
    static void runFor(Class<? extends Bidder>... bidders) {
        HashSet<Class<? extends Bidder>> allBidders = new HashSet<>(Arrays.asList(bidders));

        List<Pair<String, Integer>> results = allBidders.parallelStream()
                .map(bidder -> {

                    // Pit one bidder implementation against others.
                    HashSet<Class<? extends Bidder>> otherBidders = new HashSet<>(allBidders);
                    otherBidders.remove(bidder);

                    int winCount = otherBidders.stream()
                            .mapToInt(otherBidder -> runTrades(bidder, otherBidder))
                            .sum();

                    return Pair.of(bidder.getSimpleName(), winCount);
                })
                .sorted((pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue()))
                .collect(Collectors.toList());

        System.out.println("--- Trade simulation win counts ---");
        results.forEach(result -> System.out.println(String.format("%30s - %d", result.getKey(), result.getValue())));
    }

    /**
     * For two provided bidder classes runs TRADES_COUNT number of trading simulations with quantity and cash amount
     * selected at random based on bounds ROUNDS_MIN, ROUNDS_MAX, CASH_MIN and CASH_MAX.
     *
     * @param bidder bidder class for win counting
     * @param opponentBidder opponent bidder class
     * @return win count of bidder
     */
    private static int runTrades(Class<? extends Bidder> bidder, Class<? extends Bidder> opponentBidder) {
        return (int) IntStream.range(0, TRADES_COUNT).parallel()
                .filter(ignored -> {
                    try {
                        Bidder bidderOne = bidder.newInstance();
                        Bidder bidderTwo = opponentBidder.newInstance();

                        int quantity = ThreadLocalRandom.current().nextInt(ROUNDS_MIN, ROUNDS_MAX) * 2;
                        int cash = ThreadLocalRandom.current().nextInt(CASH_MIN, CASH_MAX);

                        return Trade.runFor(bidderOne, bidderTwo, quantity, cash).bidderOneWon();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException("Error instantiating bidder class", e);
                    }
                }).count();
    }
}
