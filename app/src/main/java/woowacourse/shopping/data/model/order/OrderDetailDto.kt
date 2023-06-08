package woowacourse.shopping.data.model.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailDto(
    val orderAt: String,
    val orderId: Int,
    val orderStatus: String,
    val payAmount: Int,
    val products: List<OrderDetailInfoDto>,
    val savedPoint: Int,
    val usedPoint: Int
)
