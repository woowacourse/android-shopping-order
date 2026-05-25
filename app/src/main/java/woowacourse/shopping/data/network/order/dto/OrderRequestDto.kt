package woowacourse.shopping.data.network.order.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    val cartItemIds: List<Long>,
)
