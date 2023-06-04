package woowacourse.shopping.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class OrderEntity(
    val orderItems: List<OrderProductEntity>,
    val originalPrice: Int,
    val usedPoints: Int,
    val orderPrice: Int
)