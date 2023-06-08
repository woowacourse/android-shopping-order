package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailEntity(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderedAt: String,
    val products: List<CartRemoteEntity>,
)
