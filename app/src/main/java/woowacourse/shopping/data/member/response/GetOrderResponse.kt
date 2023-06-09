package woowacourse.shopping.data.member.response

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.dto.OrderProduct

@Serializable
data class GetOrderResponse(
    val orderItems: List<OrderProduct>,
    val originalPrice: Int,
    val usedPoints: Int,
    val orderPrice: Int
)