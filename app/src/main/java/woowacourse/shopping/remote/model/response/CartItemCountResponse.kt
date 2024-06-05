package woowacourse.shopping.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CartItemCountResponse(
    val quantity: Int,
)
