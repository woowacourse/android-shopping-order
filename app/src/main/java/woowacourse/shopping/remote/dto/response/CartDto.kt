package woowacourse.shopping.remote.dto.response

import woowacourse.shopping.domain.model.Cart

data class CartDto(
    val id: Int,
    val quantity: Int,
    val product: ProductDto,
) {
    fun toCart(): Cart =
        Cart(
            cartId = this.id.toLong(),
            product = product.toProduct(),
            quantity = quantity,
        )
}
