package woowacourse.shopping.data.repository

import woowacourse.shopping.model.OrderList

interface OrderRepository {
    fun getOrderList(callback: (Result<OrderList>) -> Unit)
}
