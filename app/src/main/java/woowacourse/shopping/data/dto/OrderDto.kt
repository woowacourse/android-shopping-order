package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    @SerializedName("orderId")
    val orderId: Int,
    @SerializedName("orderedProducts")
    val orderedProducts: List<OrderProductResponse> = listOf(),
    @SerializedName("payment")
    val payment: PaymentResponse,
)

@Serializable
data class OrderPostRequest(
    @SerializedName("orderItems")
    val orderItems: List<OrderItem>,
    @SerializedName("payment")
    val payment: PaymentRequest,
)

@Serializable
data class OrderItem(
    @SerializedName("cartItemId")
    val cartItemId: Int,
)

@Serializable
data class OrderProductResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
)
