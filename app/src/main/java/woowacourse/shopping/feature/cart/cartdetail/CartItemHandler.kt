package woowacourse.shopping.feature.cart.cartdetail

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.CartViewModel

class CartItemHandler(
    private val viewModel: CartViewModel,
) : CartItemClickHandler,
    QuantityChangeListener {
    override fun onIncrease(cartItem: CartItem) {
        viewModel.increaseCartItemQuantity(cartItem)
    }

    override fun onDecrease(cartItem: CartItem) {
        viewModel.removeCartItemOrDecreaseQuantity(cartItem)
    }

    override fun itemDelete(cartItem: CartItem) {
        viewModel.delete(cartItem)
    }

    override fun toggleSelected(cartItem: CartItem) {
        viewModel.toggleCartItemCheck(cartItem)
    }
}
