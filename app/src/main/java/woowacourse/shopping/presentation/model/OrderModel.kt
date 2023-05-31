package woowacourse.shopping.presentation.model

data class OrderModel(
    val orderId: Int,
    val imageUrl: String,
    val orderDate: String,
    val sendPrice: Int,
)
