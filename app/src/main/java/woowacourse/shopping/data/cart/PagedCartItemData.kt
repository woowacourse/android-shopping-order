package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.domain.cart.PagedCartItems

class PagedCartItemData(
    private val cartItems: List<CartItemEntity>,
    private val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    fun toDomain(): PagedCartItems =
        PagedCartItems(
            this.cartItems.map { it.toDomain() },
            this.hasPrevious,
            this.hasNext,
        )
}
