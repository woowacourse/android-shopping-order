package woowacouse.shopping.data.repository.order

import woowacouse.shopping.model.order.Order

interface OrderRepository {
    fun addOrder(
        orderInfo: Order,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    )
}
