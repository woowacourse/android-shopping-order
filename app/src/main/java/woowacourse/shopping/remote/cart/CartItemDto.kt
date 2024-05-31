package woowacourse.shopping.remote.cart

import woowacourse.shopping.remote.product.ProductDto

data class CartItemDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto,
)
