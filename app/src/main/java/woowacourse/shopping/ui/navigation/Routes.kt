package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ProductList

@Serializable
data class ProductDetail(
    val productId: Int,
    val openedFromLastViewed: Boolean,
)

@Serializable
data object CartGraph

@Serializable
data object Cart

@Serializable
data object Recommend
