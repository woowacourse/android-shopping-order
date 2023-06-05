package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailProductDto(
    val usedPoint: Int,
    val products: List<OrderProductDto>
)

@Serializable
data class OrderProductDto(
    val productId: Int,
    val quantity: Int
)
