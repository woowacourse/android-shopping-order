package woowacourse.shopping.data.repository

import woowacourse.shopping.model.OrderList

interface OrderRepository {
    fun getOrderList(cartIds: List<Int>, callback: (Result<OrderList>) -> Unit)
}
