package woowacourse.shopping.data.remote.dto.response.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartQuantityResponse(
    val quantity: Int,
)
