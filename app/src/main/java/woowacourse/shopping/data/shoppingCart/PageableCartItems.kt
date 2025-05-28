package woowacourse.shopping.data.shoppingCart

import woowacourse.shopping.data.product.entity.CartItemEntity

class PageableCartItems(
    val cartItems: List<CartItemEntity>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
)
