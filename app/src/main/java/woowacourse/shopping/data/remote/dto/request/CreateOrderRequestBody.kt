package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequestBody(
    val cartItemIds: List<Long>,
)
