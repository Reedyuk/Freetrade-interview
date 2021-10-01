# Freetrade-interview

One subscriber listens to a list of stocks.
    
When price of stock price changes, listeners will be notified.

There are subscribers that can subscribe to prices for stocks
There is a market that publishes prices for stocks

A subscriber subscribes to multiple stocks using a watchlist, they pass a list of stock IDs and are informed when the price changes
Subscribers do not know about the market, they only know about stocks and the watchlist.

If the price of a stock changes on the market, any subscribers listening to that stock will get notified about the change

// The market functions and reports prices, however the candidate wants it to (for example, whether they poll or whether there's a push mechanism)
Should happen asynchronously - coroutines and flows
Should be demoable via unit tests

Implement a system which could take a list of Stock IDs and listen to price changes coming from **multiple** **external** markets for those stocks, updating some sort of subscriber. 
Design the system so that there can be multiple subscribers which each can listen to different lists of stocks (SSE and PSE only).
