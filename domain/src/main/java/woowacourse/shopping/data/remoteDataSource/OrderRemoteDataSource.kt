package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.model.OrderList

interface OrderRemoteDataSource {
    fun getOrder(cartIds: List<Int>, callback: (Result<OrderList>) -> Unit)
}
