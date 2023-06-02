package woowacourse.shopping.model.data.dto

data class OrderDTO(
    val orderId: Long,
    val orderPrice: Int,
    val totalAmount: Int,
    val previewName: String
)
