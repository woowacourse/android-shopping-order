package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ShoppingRoute

@Serializable
data class ProductDetailRoute(
    val productId: Long,
)

@Serializable
data object CartGraph

@Serializable
data object CartRoute

@Serializable
data object CartRecommendationRoute