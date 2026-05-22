package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

sealed interface ShoppingRoute {
    @Serializable
    data object Shopping : ShoppingRoute

    @Serializable
    data class Detail(
        val productId: String,
        val hideRecentItem: Boolean = false,
    ) : ShoppingRoute

    @Serializable
    data object Cart : ShoppingRoute

    @Serializable
    data object Recommend : ShoppingRoute
}
