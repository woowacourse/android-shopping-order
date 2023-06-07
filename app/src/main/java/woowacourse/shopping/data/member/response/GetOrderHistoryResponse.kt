package woowacourse.shopping.data.member.response

import kotlinx.serialization.Serializable

@Serializable
data class GetOrderHistoryResponse(
    val orderId: Int,
    val orderPrice: Int,
    val totalAmount: Int,
    val previewName: String
)