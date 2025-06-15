package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CartRequest(
    val productId: Long,
    val quantity: Int,
)
