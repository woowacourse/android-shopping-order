package woowacourse.shopping.domain.session

import woowacourse.shopping.domain.model.cart.SelectedCartOrder

interface PendingOrderSessionManager {
    fun start(order: SelectedCartOrder)

    fun restore(): SelectedCartOrder?

    fun clear()

    fun hasActiveSession(): Boolean
}
