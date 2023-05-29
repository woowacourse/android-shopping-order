package woowacourse.shopping.data.cart.dto

import woowacourse.shopping.data.product.dto.Product
import woowacourse.shopping.model.CartProduct

data class CartProduct(
    val id: Long,
    val product: Product,
    val quantity: Int,
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
