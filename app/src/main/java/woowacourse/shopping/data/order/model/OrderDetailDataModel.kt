package woowacourse.shopping.data.order.model

import com.google.gson.annotations.SerializedName

data class OrderDetailDataModel(
    val orderId: Int,
    val totalPrice: Int,
    val spendPoint: Int,
    val spendPrice: Int,
    val createdAt: String,
    @SerializedName("orderItemResponses") val orderItems: List<OrderProductDataModel>,
)
