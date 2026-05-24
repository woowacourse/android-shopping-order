package woowacourse.shopping.repository

import woowacourse.shopping.ui.cart.SelectedCartOrder

interface PendingOrderRepository {
    fun getPendingOrder(): SelectedCartOrder?

    fun savePendingOrder(order: SelectedCartOrder)

    fun clearPendingOrder()
}
