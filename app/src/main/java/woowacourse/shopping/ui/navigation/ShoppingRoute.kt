package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

sealed interface ShoppingRoute {
    @Serializable
    data object Catalog : ShoppingRoute

    @Serializable
    data class ProductDetail(
        val selectedProductId: Long,
        val lastViewedProductId: Long?,
    ) : ShoppingRoute

    @Serializable
    data object Cart : ShoppingRoute

    @Serializable
    data class Recommendation(
        val selectedCartItemIds: List<Long>,
    ) : ShoppingRoute
}
