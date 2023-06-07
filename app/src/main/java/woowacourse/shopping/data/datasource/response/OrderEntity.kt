package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName

data class OrderEntity(
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
