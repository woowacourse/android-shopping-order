package woowacourse.shopping.domain.cart

class PagedCartItems(
    val cartItems: List<CartItem>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
)
