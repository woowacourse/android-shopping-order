package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class OrderPostRequest(
    val orderPostInfos: List<OrderPostInfo>,
    val payment: Int,
    val point: Int,
)

data class OrderPostInfo(
    val productId: Int,
    val quantity: Int,
)

data class OrderResponse(
    val orderId: Int,
    val orderedProducts: List<OrderProductResponse>,
    @SerializedName("payment")
    val totalPrice: Int,
    @SerializedName("point")
    val usedPoint: Int,
)

data class OrderProductResponse(
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String,
)
