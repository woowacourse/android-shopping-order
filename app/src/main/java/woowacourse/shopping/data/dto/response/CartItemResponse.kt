package woowacourse.shopping.data.dto.response

import woowacourse.shopping.domain.Cart

data class CartItemResponse(
    val id: Int,
    val quantity: Int,
    val product: ProductItemResponse,
) {
    fun toCart(): Cart =
        Cart(
            cartId = this.id.toLong(),
            product = product.toProduct(),
            quantity = quantity,
        )
}
