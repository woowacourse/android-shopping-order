package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.page.Page

interface OrderRepository {
    fun saveOrder(
        order: Order,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    )

    fun getOrders(
        page: Page,
        onSuccess: (List<Order>) -> Unit,
        onFailed: (Throwable) -> Unit,
    )
}
