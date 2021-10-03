package Models

typealias StockID = String

data class Stock(
    val id: StockID,
    var price: Double
)
