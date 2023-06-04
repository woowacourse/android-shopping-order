package woowacourse.shopping.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
//data class OrdersResponse(
//    @SerialName("pageInfo")
//    val pageInfo: PageInfoResponse,
//    @SerialName("orders")
//    val orders: List<OrderResponse> = listOf(),
//)

@Serializable
data class OrderResponse(
    @SerialName("orderId")
    val orderId: Int,
    @SerialName("orderedProducts")
    val orderedProducts: List<OrderProductResponse> = listOf(),
    @SerialName("payment")
    val payment: PaymentResponse,
)

@Serializable
data class OrderProductResponse(
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
)

@Serializable
data class OrderPostRequest(
    @SerialName("orderItems")
    val orderItems: List<OrderItem>,
    @SerialName("payment")
    val payment: PaymentRequest,
)

@Serializable
data class OrderItem(
    @SerialName("cartItemId")
    val cartItemId: Int,
)
