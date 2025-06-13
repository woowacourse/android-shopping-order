package woowacourse.shopping.domain.model

data class PagedCartItems(
    val cartItems: List<CartItem>,
    val hasMore: Boolean,
)
