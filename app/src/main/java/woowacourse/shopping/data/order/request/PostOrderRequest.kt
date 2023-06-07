package woowacourse.shopping.data.order.request

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.dto.CartId

@Serializable
data class PostOrderRequest(
    val cartItemIds: List<CartId>,
    val originalPrice: Int,
    val points: Int
)