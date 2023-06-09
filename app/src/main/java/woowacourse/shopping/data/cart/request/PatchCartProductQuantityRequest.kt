package woowacourse.shopping.data.cart.request

import kotlinx.serialization.Serializable

@Serializable
data class PatchCartProductQuantityRequest(
    val quantity: Int
)