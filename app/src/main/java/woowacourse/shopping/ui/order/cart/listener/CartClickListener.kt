package woowacourse.shopping.ui.order.cart.listener

import woowacourse.shopping.ui.home.listener.QuantityClickListener

interface CartClickListener : QuantityClickListener {
    fun onCheckBoxClick(cartItemId: Int)

    fun onCartItemClick(productId: Int)

    fun onDeleteButtonClick(cartItemId: Int)
}
