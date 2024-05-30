package woowacourse.shopping.presentation.ui.cart.selection

import woowacourse.shopping.presentation.ui.counter.CounterHandler

interface SelectionCountHandler : CounterHandler {
    override fun increaseCount(productId: Long)

    override fun decreaseCount(productId: Long)
}
