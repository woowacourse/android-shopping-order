package woowacourse.shopping.data.model.order

import kotlinx.serialization.Serializable

@Serializable
data class ContentDto(
    val orderId: Int,
    val payAmount: Int,
    val orderAt: String,
    val orderStatus: String,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
)
