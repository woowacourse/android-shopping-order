package woowacourse.shopping.data.cart.request

import kotlinx.serialization.Serializable

@Serializable
data class PostCartProductRequest(
    val productId: Int,
    val quantity: Int
)