package woowacourse.shopping.data.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailResponse(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderedAt: String,
    val products: List<CartResponse>,
)
