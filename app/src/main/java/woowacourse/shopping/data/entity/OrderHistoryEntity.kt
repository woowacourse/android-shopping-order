package woowacourse.shopping.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class OrderHistoryEntity(
    val orderId: Int,
    val orderPrice: Int,
    val totalAmount: Int,
    val previewName: String
)