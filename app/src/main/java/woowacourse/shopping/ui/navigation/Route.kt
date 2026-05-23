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
data class CartRecommendation(
    val selectedCartItemIds: List<Long>,
)

@Serializable
object Payment
