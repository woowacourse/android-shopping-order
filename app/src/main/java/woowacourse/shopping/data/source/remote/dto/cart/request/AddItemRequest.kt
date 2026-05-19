package woowacourse.shopping.data.source.remote.dto.cart.request

import kotlinx.serialization.Serializable

@Serializable
data class AddItemRequest(
    val productId: Long,
    val quantity: Int,
)
