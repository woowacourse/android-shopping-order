package woowacourse.shopping.presentation.ui.cart.recommendation

import woowacourse.shopping.presentation.ui.counter.CounterHandler

interface RecommendItemCountHandler : CounterHandler {
    override fun increaseCount(productId: Long)

    override fun decreaseCount(productId: Long)
}
