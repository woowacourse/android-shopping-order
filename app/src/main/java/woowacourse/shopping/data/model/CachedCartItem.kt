package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CachedCartItem(
    val cartId: Long,
    val productId: Long,
    val quantity: Int,
)
