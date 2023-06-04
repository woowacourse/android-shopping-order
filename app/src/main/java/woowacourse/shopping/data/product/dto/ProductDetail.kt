package woowacourse.shopping.data.product.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetail(
    val product: Product,
    val cartItem: CartItem?,
)
