package woowacourse.shopping.data.model.cart

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.model.product.ProductDto

@Serializable
data class CartProductDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto
)
