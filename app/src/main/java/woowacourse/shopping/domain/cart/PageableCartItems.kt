package woowacourse.shopping.domain.cart

import woowacourse.shopping.domain.product.CartItem

class PageableCartItems(
    val cartItems: List<CartItem>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    companion object
}
