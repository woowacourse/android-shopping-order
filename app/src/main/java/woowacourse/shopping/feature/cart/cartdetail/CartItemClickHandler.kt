package woowacourse.shopping.feature.cart.cartdetail

import woowacourse.shopping.domain.model.CartItem

interface CartItemClickHandler {
    fun itemDelete(cartItem: CartItem)

    fun toggleSelected(cartItem: CartItem)
}
