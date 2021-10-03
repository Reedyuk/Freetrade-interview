import Models.Stock
import Services.MarketService
import app.cash.turbine.test
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
// Can't use runBlockingTest due to use of delay in flow.
fun runTest(test: suspend () -> Unit) = runBlocking { test() }

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalTime
class WatchListTests {

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    // Test subscribing to a single stock
    @Test
    fun testSingleStockSubscription() = runTest {
        val ftseFlow: Flow<Stock> = listOf(
            Stock("LLY", 0.02), Stock("LLY", 2.50), Stock("RR", 3.50), Stock("LLY", 10.22)
        ).asFlow()
        val marketService = MarketService(ftseFlow, emptyFlow(), emptyFlow())
        val watchList = WatchList(listOf("LLY"), marketService)

        watchList.subscriptions.test {
            val price = awaitItem()
            assertEquals(0.02, price.price)
            assertEquals("LLY", price.id)

            val adjustedPrice = awaitItem()
            assertEquals(2.50, adjustedPrice.price)
            assertEquals("LLY", adjustedPrice.id)

            val adjustedPriceFinal = awaitItem()
            assertEquals(10.22, adjustedPriceFinal.price)
            assertEquals("LLY", adjustedPriceFinal.id)

            awaitComplete()
        }
    }

    // Subscribe to multiple stocks, only be informed of a change of the relevant stock.
    @Test
    fun testMultipleStockSubscriptions() = runTest {
        val ftseFlow: Flow<Stock> = listOf(
            Stock("ADM", 0.02), Stock("LLY", 2.50), Stock("RR", 3.50), Stock("LLY", 10.22), Stock("ITV", 22.22)
        ).asFlow()
        val marketService = MarketService(ftseFlow, emptyFlow(), emptyFlow())
        val watchList = WatchList(listOf("LLY", "RR"), marketService)

        watchList.subscriptions.test {
            val onePrice = awaitItem()
            assertEquals(2.50, onePrice.price)
            assertEquals("LLY", onePrice.id)

            val secondPrice = awaitItem()
            assertEquals(3.50, secondPrice.price)
            assertEquals("RR", secondPrice.id)

            val thirdPrice = awaitItem()
            assertEquals(10.22, thirdPrice.price)
            assertEquals("LLY", thirdPrice.id)

            awaitComplete()
        }
    }

    // Try subscribing to a ftse and a nasdaq markets and mix the streams.
    @Test
    fun testMultipleExchanges() = runTest {
        val ftseFlow: Flow<Stock> = listOf(
            Stock("ADM", 0.02), Stock("LLY", 2.50), Stock("RR", 3.50), Stock("LLY", 10.22), Stock("ITV", 22.22)
        ).asFlow().onEach { delay(200) }
        val nasdaqFlow: Flow<Stock> = listOf(
            Stock("TSLA", 0.02), Stock("MSFT", 2.50), Stock("TSLA", 3.50), Stock("AAPL", 10.22), Stock("TSLA", 22.22)
        ).asFlow().onEach { delay(200) }
        val marketService = MarketService(ftseFlow, nasdaqFlow, emptyFlow())
        val watchList = WatchList(listOf("LLY", "TSLA"), marketService)

        watchList.subscriptions.test {
            val onePrice = awaitItem()
            assertEquals("TSLA", onePrice.id)
            assertEquals(0.02, onePrice.price)

            val secondPrice = awaitItem()
            assertEquals(2.50, secondPrice.price)
            assertEquals("LLY", secondPrice.id)

            val thirdPrice = awaitItem()
            assertEquals(3.50, thirdPrice.price)
            assertEquals("TSLA", thirdPrice.id)

            val fourthPrice = awaitItem()
            assertEquals(10.22, fourthPrice.price)
            assertEquals("LLY", fourthPrice.id)

            val fifthPrice = awaitItem()
            assertEquals(22.22, fifthPrice.price)
            assertEquals("TSLA", fifthPrice.id)

            awaitComplete()
        }
    }
}
