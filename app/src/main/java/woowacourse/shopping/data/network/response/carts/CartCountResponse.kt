package woowacourse.shopping.data.network.response.carts

import kotlinx.serialization.Serializable

@Serializable
data class CartCountResponse(
    val quantity: Int,
)
