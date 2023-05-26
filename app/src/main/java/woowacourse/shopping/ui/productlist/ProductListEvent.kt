package woowacourse.shopping.ui.productlist

import woowacourse.shopping.ui.customview.CounterEvent

interface ProductListEvent : CounterEvent {
    fun onClick(productId: Long)
    fun onClickAddToCartButton(productId: Long)
}
