package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderHistoryDto(
    val id: Int,
    val orderItems: List<OrderProductDto>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String,
)
