package woowacourse.shopping.data.dto

data class OrderPostRequest(
    val orderItems: List<OrderItem>,
    val payment: PaymentRequest,
)

data class OrderItem(
    val cartItemId: Int,
)

data class OrderResponse(
    val orderId: Int,
    val orderedProducts: List<OrderProductResponse>,
    val payment: PaymentResponse,
)

data class OrderProductResponse(
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String,
)
