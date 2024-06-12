package woowacourse.shopping.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val id: Int,
    val quantity: Int,
    val product: ProductResponse,
)
