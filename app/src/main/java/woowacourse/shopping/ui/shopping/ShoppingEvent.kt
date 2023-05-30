package woowacourse.shopping.ui.shopping

import woowacourse.shopping.ui.customview.CounterEvent

interface ShoppingEvent : CounterEvent {
    fun onClick(productId: Long)
    fun onClickAddToCartButton(productId: Long)
}
