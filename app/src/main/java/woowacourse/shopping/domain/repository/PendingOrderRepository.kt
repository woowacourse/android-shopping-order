package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.cart.SelectedCartOrder

interface PendingOrderRepository {
    fun getPendingOrder(): SelectedCartOrder?

    fun savePendingOrder(order: SelectedCartOrder)

    fun clearPendingOrder()
}
