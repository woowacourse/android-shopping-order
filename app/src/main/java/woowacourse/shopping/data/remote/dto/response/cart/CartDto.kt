package woowacourse.shopping.data.remote.dto.response.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartDto(
    val id: Long,
    val product: CartProductDto,
    val quantity: Int,
)
