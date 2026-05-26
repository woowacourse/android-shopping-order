package woowacourse.shopping.data.remote.cart

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.remote.product.ProductResponseDto

@Serializable
data class CartItemResponseDto(
    val id: Long,
    val quantity: Int,
    val product: ProductResponseDto,
)
