package woowacourse.shopping.presentation.model

data class OrderModel(
    val firstProductName: String,
    val totalCount: Int,
    val orderId: Int,
    val imageUrl: String,
    val orderDate: String,
    val sendPrice: Int,
)
