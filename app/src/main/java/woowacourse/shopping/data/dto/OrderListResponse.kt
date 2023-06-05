package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderListResponse(
    val orders: List<OrderHistoryDto>,
    val lastOrderId: Int,
)
