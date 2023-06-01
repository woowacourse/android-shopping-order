package woowacourse.shopping.data.dto

data class OrderPostRequest(
    val orderPostInfos: List<OrderPostInfo>,
    val payment: PaymentRequest,
)

data class OrderPostInfo(
    val productId: Int,
    val quantity: Int,
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
