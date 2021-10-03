import Services.MarketService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter

// ["TSLA", "BTC"]
@FlowPreview
class WatchList(private val stockIds: List<String>, marketService: MarketService) {

    val subscriptions =
        // TODO: First for now but really should handle multiple stock markets.
        marketService.markets.filter {
            stockIds.contains(it.id)
        }

}
