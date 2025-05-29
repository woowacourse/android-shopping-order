package woowacourse.shopping.domain.cart

class PageableCartItems(
    val cartItems: List<CartItem>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    companion object
}
