package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.DataCallback

interface OrderRepository {
    fun createOrder(
        cartItemIds: List<Int>,
        dataCallback: DataCallback<Unit>,
    )
}
