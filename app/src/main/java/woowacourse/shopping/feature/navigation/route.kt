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

//@Serializable
//data class Recommend(
//    val cartContentIds: List<CartContentId>
//)
