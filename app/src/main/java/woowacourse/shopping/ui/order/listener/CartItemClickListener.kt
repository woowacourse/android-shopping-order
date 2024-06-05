package woowacourse.shopping.ui.order.listener

import woowacourse.shopping.ui.home.listener.QuantityClickListener

interface CartItemClickListener : QuantityClickListener {
    fun onCheckBoxClick(cartItemId: Int)

    fun onCartItemClick(productId: Int)

    fun onDeleteButtonClick(cartItemId: Int)
}
