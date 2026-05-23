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

    data object Cart: ShoppingRoute
    data object Recommendation: ShoppingRoute
}
