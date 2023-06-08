package woowacourse.shopping.dto

import woowacourse.shopping.model.CartProduct

fun List<CartProductDto>.toDomain() = this.map { it.toDomain() }

class CartProductDto(
    var id: Int = 0,
    var quantity: Int = 0,
    var product: ProductDto
) {
    fun toDomain(): CartProduct = CartProduct(
        id = id,
        quantity = quantity,
        product = product.toDomain()
    )
}
