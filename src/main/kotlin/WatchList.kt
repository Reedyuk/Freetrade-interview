import Services.MarketService
import kotlinx.coroutines.flow.filter

// ["TSLA", "BTC"]
class WatchList(private val stockIds: List<String>, marketService: MarketService) {

    val subscriptions =
        // TODO: First for now but really should handle multiple stock markets.
        marketService.markets.first().stocks
            .filter {
                stockIds.contains(it.id)
            }
}
