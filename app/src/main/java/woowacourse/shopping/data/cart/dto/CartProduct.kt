package woowacourse.shopping.data.cart.dto

import woowacourse.shopping.data.product.dto.Product
import woowacourse.shopping.model.CartProduct

data class CartProduct(
    val id: Long,
    val quantity: Int,
    val product: Product,
) {
    fun toDomainCartProduct(): CartProduct {
        return CartProduct(
            cartId = id,
            product = product.toDomainProduct(),
            quantity = quantity,
            isChecked = true,
        )
    }
}
