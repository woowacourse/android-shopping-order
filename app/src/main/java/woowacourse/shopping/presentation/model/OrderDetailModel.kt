package woowacourse.shopping.presentation.model

data class OrderDetailModel(
    val orderId: Int,
    val totalPrice: Int,
    val spendPoint: Int,
    val spendPrice: Int,
    val orderDate: String,
    val orderItems: OrderProductsModel
)
