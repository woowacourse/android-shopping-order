package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.entity.CartItemEntity

class PageableCartItemData(
    val cartItems: List<CartItemEntity>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
)
