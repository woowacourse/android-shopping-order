package woowacourse.shopping.data.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val cartItemIds: List<Long>,
)
