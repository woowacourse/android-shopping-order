package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    val usedPoint: Int,
    val products: List<OrderRequestProductDto>
)
