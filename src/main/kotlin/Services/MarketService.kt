package Services

import Models.Stock
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf

// Hardcoded the 'market' feeds because it will never change e.g. a new stock market doesn't just open up.
class MarketService(ftseFeed: Flow<Stock>, nasdaqFeed: Flow<Stock>, cryptoFeed: Flow<Stock>) {

    @FlowPreview
    val markets: Flow<Stock> = flowOf(ftseFeed, nasdaqFeed, cryptoFeed).flattenMerge()
}
