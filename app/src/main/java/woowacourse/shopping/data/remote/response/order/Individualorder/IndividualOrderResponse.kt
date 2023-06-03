package woowacourse.shopping.data.remote.response.order.Individualorder

import com.google.gson.annotations.SerializedName

data class IndividualOrderResponse(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("earnedPoint")
    val earnedPoint: Int,
    @SerializedName("orderId")
    val orderId: Int,
    @SerializedName("orderItems")
    val orderItems: List<OrderItem>,
    @SerializedName("totalPrice")
    val totalPrice: Int,
    @SerializedName("usedPoint")
    val usedPoint: Int
)
