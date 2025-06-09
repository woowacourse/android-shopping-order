package woowacourse.shopping.data.order.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    @SerialName("cartItemIds")
    val cartItemIds: List<Long>,
)
