package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val orderId: Int,
    val orderedProducts: List<OrderProductResponse> = listOf(),
    val payment: PaymentResponse,
)

@Serializable
data class OrderPostRequest(
    val orderItems: List<OrderItem>,
    val payment: PaymentRequest,
)

@Serializable
data class OrderItem(
    val cartItemId: Int,
)

@Serializable
data class OrderProductResponse(
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String,
)
