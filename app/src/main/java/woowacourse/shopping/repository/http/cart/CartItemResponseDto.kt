package woowacourse.shopping.repository.http.cart

import kotlinx.serialization.Serializable
import woowacourse.shopping.repository.http.product.ProductResponseDto

@Serializable
data class CartItemResponseDto(
    val id: Long,
    val quantity: Int,
    val product: ProductResponseDto,
)
