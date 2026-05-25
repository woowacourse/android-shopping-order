package woowacourse.shopping.data.remote.server.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class PostOrderRequest(
    val cartItemIds: List<Long>,
)
