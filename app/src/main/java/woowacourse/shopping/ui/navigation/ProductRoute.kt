package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

sealed interface ProductRoute {
    @Serializable
    data object ProductList : ProductRoute

    @Serializable
    data class Detail(
        val productId: Int,
        val openedFromLastViewed: Boolean,
    ) : ProductRoute
}
