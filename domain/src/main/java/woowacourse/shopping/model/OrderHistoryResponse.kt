package woowacourse.shopping.model

import com.google.gson.annotations.SerializedName

data class OrderHistoryResponse(
    @SerializedName("orders")
    val orderHistories: List<OrderHistory>,
    val lastOrderId: Long
)
