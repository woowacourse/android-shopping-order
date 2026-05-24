package woowacourse.shopping.repository

import woowacourse.shopping.model.SelectedCartOrder

interface PendingOrderRepository {
    fun getPendingOrder(): SelectedCartOrder?

    fun savePendingOrder(order: SelectedCartOrder)

    fun clearPendingOrder()
}
