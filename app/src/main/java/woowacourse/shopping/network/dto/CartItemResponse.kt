package woowacourse.shopping.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int
)
