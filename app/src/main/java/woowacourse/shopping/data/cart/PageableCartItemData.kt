package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.domain.cart.PageableCartItems

class PageableCartItemData(
    val cartItems: List<CartItemEntity>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    fun toDomain(): PageableCartItems =
        PageableCartItems(
            this.cartItems.map { it.toDomain() },
            this.hasPrevious,
            this.hasNext,
        )
}
