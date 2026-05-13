package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
class CartItemDto(
    val id: Int,
    val quantity: Int,
    val product: ProductResponseDto,
)
