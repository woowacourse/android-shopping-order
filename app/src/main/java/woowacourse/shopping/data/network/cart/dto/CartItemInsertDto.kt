package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemInsertDto(
    val productId: Long,
    val quantity: Int,
)
