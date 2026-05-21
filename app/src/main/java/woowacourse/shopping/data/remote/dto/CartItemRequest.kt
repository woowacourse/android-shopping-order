package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)
