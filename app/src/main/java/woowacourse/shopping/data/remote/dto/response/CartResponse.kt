package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val id: Int,
    val quantity: Int,
    val product: ProductResponse,
)
