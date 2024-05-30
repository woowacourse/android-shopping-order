package woowacourse.shopping.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
