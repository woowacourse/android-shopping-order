package woowacourse.shopping.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PostCartItemRequest(
    val productId: Int,
    val quantity: Int,
)
