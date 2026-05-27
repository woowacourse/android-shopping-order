package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object ProductList

@Serializable
data class ProductDetail(
    val productId: Long,
)

@Serializable
object Cart

@Serializable
object Settings

@Serializable
data class CartRecommendation(
    val selectedCartItemIds: List<Long>,
)

@Serializable
data class Payment(
    val productIds: List<Long> = emptyList(),
    val quantities: List<Int> = emptyList(),
)
