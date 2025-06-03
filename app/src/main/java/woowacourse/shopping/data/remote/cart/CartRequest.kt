package woowacourse.shopping.data.remote.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartRequest(
    val productId: Long,
    val quantity: Int,
)
