package woowacourse.shopping.domain.cart

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product

data class ShoppingCart(
    val id: Long,
    val product: Product,
    val quantity: Quantity,
) {
    val productId: Long
        get() = product.id
}
