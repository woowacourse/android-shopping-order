package woowacourse.shopping.presentation.view.order.cart.event

import woowacourse.shopping.presentation.ui.layout.QuantityChangeListener

interface CartStateListener : QuantityChangeListener {
    fun onDeleteProduct(cartId: Long)

    fun onCheckOrder(cartId: Long)

    fun onSwitchAllOrder()
}
