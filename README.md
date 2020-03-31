# Bidder Bot
Seven implementations of a bidding bot with following interface:
```java
public interface Bidder {
    void init(int quantity, int cash);
    int placeBid();
    void bids(int own, int other);
}
```
with simple simulation environment and some unit test.  

### How Simulation Works

Each bidder implementation is evaluated by simulating 100 trades with each other implementation. Each trade is executed
with random amount of quantity units and cash.

After all simulations are complete, win counts are collected and presented with corresponding bidder names in order from
most to least efficient.

### Strategy

As the winning condition is to have more quantity units than opponent has at the end of trading, then to win for sure 
the bidder have to overbid the opponent in half of trading rounds plus one. All overbid strategies that I've implemented
are using 1/3 of cash budget to try to overbid opponent in first 2/3 of rounds.

Of all bidder implementations ``LinearRegressionBidder`` shows the best results in simulation. 

### Ways To Improve
  
Devise a strategy that calculates expected opponent's bid value based on bidder and opponent "worth of winning" 
function for each trading round.
 
### How To Run
 
Just run this in project root directory:
```shell script
./gradlew run
```