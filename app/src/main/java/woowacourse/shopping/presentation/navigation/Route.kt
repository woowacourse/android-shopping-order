package woowacourse.shopping.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object ShoppingScreen

@Serializable
data class DetailScreen(
    val productId: Long,
)

@Serializable
object CartScreen

@Serializable
data class RecommendScreen(
    val productsId: List<Long>,
)
