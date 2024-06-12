package woowacourse.shopping.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PostOrderRequest(
    val cartItemIds: List<Int>,
)
