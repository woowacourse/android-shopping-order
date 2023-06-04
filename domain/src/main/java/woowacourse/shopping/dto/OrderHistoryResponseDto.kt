package woowacourse.shopping.dto

import com.google.gson.annotations.SerializedName

data class OrderHistoryResponseDto(
    @SerializedName("orders")
    val orderHistories: List<OrderHistoryDto>,
    val lastOrderId: Long
)
