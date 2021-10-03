package Models

import kotlinx.coroutines.flow.Flow

data class Market(
    val name: String,
    val stocks: Flow<Stock>
)
