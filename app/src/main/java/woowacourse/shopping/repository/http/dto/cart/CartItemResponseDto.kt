package woowacourse.shopping.repository.http.dto.cart

import kotlinx.serialization.Serializable
import woowacourse.shopping.repository.http.dto.product.ProductResponseDto

@Serializable
data class CartItemResponseDto(
    val id: Long,
    val quantity: Int,
    val product: ProductResponseDto,
)
