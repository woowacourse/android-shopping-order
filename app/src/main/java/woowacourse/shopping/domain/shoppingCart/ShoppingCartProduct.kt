package woowacourse.shopping.domain.shoppingCart

import woowacourse.shopping.domain.product.Product

data class ShoppingCartProduct(
    val id: Long,
    val product: Product,
    val quantity: Int,
) {
    val price: Int get() = product.price * quantity
}
