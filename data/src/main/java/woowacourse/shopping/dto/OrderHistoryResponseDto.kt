package woowacourse.shopping.dto

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.model.OrderHistories

data class OrderHistoryResponseDto(
    @SerializedName("orders")
    val orderHistories: List<OrderHistoryDto>,
    val lastOrderId: Long
) {
    fun toDomain() = OrderHistories(
        orderHistories = orderHistories.map { it.toDomain() },
        lastOrderId = lastOrderId
    )
}
