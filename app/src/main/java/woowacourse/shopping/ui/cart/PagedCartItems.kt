package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.model.cart.CartItems

data class PagedCartItems(
    val items: CartItems,
    val isLast: Boolean = false,
    val isFirst: Boolean = true,
    val totalPages: Int = 1,
)
