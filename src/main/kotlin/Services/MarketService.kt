package Services

import Models.Market
import Models.Stock
import kotlinx.coroutines.flow.Flow

// Hardcoded the 'market' feeds because it will never change e.g. a new stock market doesn't just open up.
class MarketService(ftseFeed: Flow<Stock>, nasdaqFeed: Flow<Stock>, cryptoFeed: Flow<Stock>) {
    val markets: List<Market>

    init {
        markets = listOf(
            Market("FTSE", ftseFeed),
            Market("NASDAQ", nasdaqFeed),
            Market("CRYPTO", cryptoFeed)
        )
    }
}
