package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int
)
