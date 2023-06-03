package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.model.OrderProductEntity

data class OrderResponse(
    @SerializedName("orderId")
    val orderId: Long,
    @SerializedName("createdAt")
    val orderedTime: String,
    @SerializedName("orderItems")
    val orderProducts: List<OrderProductEntity>,
    @SerializedName("totalPrice")
    val totalPrice: Long,
    @SerializedName("usedPoint")
    val usedPoint: Long,
    @SerializedName("earnedPoint")
    val earnedPoint: Long,
)
