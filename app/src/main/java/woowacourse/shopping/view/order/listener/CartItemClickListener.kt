package woowacourse.shopping.view.order.listener

import woowacourse.shopping.view.home.listener.QuantityClickListener

interface CartItemClickListener : QuantityClickListener {
    fun onCheckBoxClick(cartItemId: Int)

    fun onCartItemClick(productId: Int)

    fun onDeleteButtonClick(cartItemId: Int)
}
