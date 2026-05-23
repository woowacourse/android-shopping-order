package woowacourse.shopping.feature.navigation

import kotlinx.serialization.Serializable
import woowacourse.shopping.feature.cart.CartContentId

@Serializable
object ProductList

@Serializable
data class ProductDetail(
    val id: Long,
    val recentProductId: Long?
)

@Serializable
object Cart

@Serializable
object Setting

@Serializable
data class Recommend(
    val cartContentIds: List<Long>
)

@Serializable
data class Payment(
    val cartContentIds: List<Long>
)
