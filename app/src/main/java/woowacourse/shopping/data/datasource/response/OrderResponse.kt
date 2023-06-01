package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.model.OrderProductEntity

data class OrderResponse(
    val orderId: Long,
    @SerializedName("createAt")
    val orderedTime: String?,
    @SerializedName("orderItems")
    val orderDetails: List<OrderProductEntity>,
    val totalPrice: Long,
    val usedPoint: Long,
    val earnedPoint: Long,
)
